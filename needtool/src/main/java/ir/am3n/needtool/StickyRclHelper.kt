package ir.am3n.needtool

import android.content.Context
import android.graphics.PointF
import android.os.Handler
import android.os.Looper
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.util.SparseBooleanArray
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.HashSet
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min





private const val sectioning_adapter_tag_key_view_viewholder = 1587987512



//***********************************************************************



/**
 * SectioningAdapter
 * Represents a list of sections, each containing a list of items and optionally a header and or footer item.
 * SectioningAdapter may be used with a normal RecyclerView.LinearLayoutManager but is meant for use with
 * StickyHeaderLayoutManager to allow for sticky positioning of header items.
 *
 *
 * When invalidating the adapter's contents NEVER use RecyclerView.Adapter.notify* methods. These methods
 * aren't aware of the section information and internal state of SectioningAdapter. As such, please
 * use the SectioningAdapter.notify* methods.
 *
 *
 * SectioningAdapter manages four types of items: TYPE_HEADER, TYPE_ITEM, TYPE_FOOTER and TYPE_GHOST_HEADER.
 * Headers are the optional first item in a section. A section then has some number of items in it,
 * and an optional footer. The ghost header is a special item used for layout mechanics. It can
 * be ignored by SectioningAdapter subclasses - but it is made externally accessible just in case.
 */
abstract class SectioningAdapter : RecyclerView.Adapter<SectioningAdapter.ViewHolder?>() {

    open class Section {
        var adapterPosition = 0 // adapterPosition of first item (the header) of this sections = 0
        var numberOfItems = 0 // number of items (not including header or footer) = 0
        var length = 0 // total number of items in sections including header and footer = 0
        var hasHeader = false // if true, sections has a header = false
        var hasFooter = false // if true, sections has a footer = false
    }

    private class SectionSelectionState {
        var section = false
        var items = SparseBooleanArray()
        var footer = false
    }

    private var sections: ArrayList<Section>? = ArrayList()
    private val collapsedSections = HashMap<Int, Boolean?>()
    private var selectionStateBySection = HashMap<Int, SectionSelectionState?>()
    private var sectionIndicesByAdapterPosition = IntArray(0)
    private var totalNumberOfItems = 0
    private var mainThreadHandler: Handler? = null

    open class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {

        var section = 0

        var numberOfItemsInSection = 0

        val itemViewBaseType: Int get() = unmaskBaseViewType(itemViewType)

        val itemViewUserType: Int get() = unmaskUserViewType(itemViewType)

        open val isHeader: Boolean get() = false

        open val isGhostHeader: Boolean get() = false

        open val isFooter: Boolean get() = false

    }

    open class ItemViewHolder(itemView: View?) : ViewHolder(itemView) {
        var positionInSection = 0
    }

    open class HeaderViewHolder(itemView: View?) :
        ViewHolder(itemView) {
        override val isHeader: Boolean get() = true
    }

    class GhostHeaderViewHolder(itemView: View?) :
        ViewHolder(itemView) {
        override val isGhostHeader: Boolean get() = true
    }

    open class FooterViewHolder(itemView: View?) :
        ViewHolder(itemView) {
        override val isFooter: Boolean get() = true
    }

    /**
     * @return Number of sections
     */
    open fun getNumberOfSections(): Int {
        return 0
    }

    /**
     * @param sectionIndex index of the section in question
     * @return the number of items in the specified section
     */
    open fun getNumberOfItemsInSection(sectionIndex: Int): Int {
        return 0
    }

    /**
     * @param sectionIndex index of the section in question
     * @return true if this section has a header
     */
    open fun doesSectionHaveHeader(sectionIndex: Int): Boolean {
        return false
    }

    /**
     * For scenarios with multiple types of headers, override this to return an integer in range [0,255] specifying a custom type for this header.
     * The value you return here will be passes to onCreateHeaderViewHolder and onBindHeaderViewHolder as the 'userType'
     *
     * @param sectionIndex the header's section
     * @return the custom type for this header in range [0,255]
     */
    open fun getSectionHeaderUserType(sectionIndex: Int): Int {
        return 0
    }

    /**
     * @param sectionIndex index of the section in question
     * @return true if this section has a footer
     */
    open fun doesSectionHaveFooter(sectionIndex: Int): Boolean {
        return false
    }

    /**
     * For scenarios with multiple types of footers, override this to return an integer in range [0, 255] specifying a custom type for this footer.
     * The value you return here will be passes to onCreateFooterViewHolder and onBindFooterViewHolder as the 'userType'
     *
     * @param sectionIndex the footer's section
     * @return the custom type for this footer in range [0,255]
     */
    open fun getSectionFooterUserType(sectionIndex: Int): Int {
        return 0
    }

    /**
     * For scenarios with multiple types of items, override this to return an integer in range [0,255] specifying a custom type for the item at this position
     * The value you return here will be passes to onCreateItemViewHolder and onBindItemViewHolder as the 'userType'
     *
     * @param sectionIndex the items's section
     * @param itemIndex    the position of the item in the section
     * @return the custom type for this item in range [0,255]
     */
    open fun getSectionItemUserType(sectionIndex: Int, itemIndex: Int): Int {
        return 0
    }

    /**
     * Called when a ViewHolder is needed for a section item view
     *
     * @param parent       The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param itemUserType If getSectionItemUserType is overridden to vend custom types, this will be the specified type
     * @return A new ItemViewHolder holding an item view
     */
    open fun onCreateItemViewHolder(parent: ViewGroup?, itemUserType: Int): ItemViewHolder? {
        return null
    }

    /**
     * Called when a ViewHolder is needed for a section header view
     *
     * @param parent         The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param headerUserType If getSectionHeaderUserType is overridden to vend custom types, this will be the specified type
     * @return A new HeaderViewHolder holding a header view
     */
    open fun onCreateHeaderViewHolder(parent: ViewGroup?, headerUserType: Int): HeaderViewHolder? {
        return null
    }

    /**
     * Called when a ViewHolder is needed for a section footer view
     *
     * @param parent         The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param footerUserType If getSectionHeaderUserType is overridden to vend custom types, this will be the specified type
     * @return A new FooterViewHolder holding a footer view
     */
    open fun onCreateFooterViewHolder(parent: ViewGroup?, footerUserType: Int): FooterViewHolder? {
        return null
    }

    /**
     * Called when a ViewHolder is needed for a section ghost header view
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @return A new GhostHeaderViewHolder holding a ghost header view
     */
    fun onCreateGhostHeaderViewHolder(parent: ViewGroup): GhostHeaderViewHolder {
        val ghostView = View(parent.context)
        return GhostHeaderViewHolder(ghostView)
    }

    /**
     * Called to display item data at particular position
     *
     * @param viewHolder   the view holder to update
     * @param sectionIndex the index of the section containing the item
     * @param itemIndex    the index of the item in the section where 0 is the first item
     * @param itemUserType if getSectionItemUserType is overridden to provide custom item types, this will be the type for this item
     */
    open fun onBindItemViewHolder(viewHolder: ItemViewHolder?, sectionIndex: Int, itemIndex: Int, itemUserType: Int) {

    }

    /**
     * Called to display header data for a particular section
     *
     * @param viewHolder     the view holder to update
     * @param sectionIndex   the index of the section containing the header to update
     * @param headerUserType if getSectionHeaderUserType is overridden to provide custom header types, this will be the type for this header
     */
    open fun onBindHeaderViewHolder(viewHolder: HeaderViewHolder?, sectionIndex: Int, headerUserType: Int) {

    }

    /**
     * Called to update the ghost header for a particular section. Note, most implementations will not need to ever touch the ghost header.
     *
     * @param viewHolder   the view holder to update
     * @param sectionIndex the index of the section containing the ghost header to update
     */
    open fun onBindGhostHeaderViewHolder(viewHolder: GhostHeaderViewHolder?, sectionIndex: Int) {

    }

    /**
     * Called to display footer data for a particular section
     *
     * @param viewHolder     the view holder to update
     * @param sectionIndex   the index of the section containing the footer to update
     * @param footerUserType if getSectionFooterUserType is overridden to provide custom footer types, this will be the type for this footer
     */
    open fun onBindFooterViewHolder(viewHolder: FooterViewHolder?, sectionIndex: Int, footerUserType: Int) {

    }

    /**
     * Given a "global" adapter adapterPosition, determine which sections contains that item
     *
     * @param adapterPosition an adapter adapterPosition from 0 to getItemCount()-1
     * @return the index of the sections containing that item
     */
    fun getSectionForAdapterPosition(adapterPosition: Int): Int {
        if (sections == null) {
            buildSectionIndex()
        }
        if (itemCount == 0) {
            return -1
        }
        if (adapterPosition < 0 || adapterPosition >= itemCount) {
            throw IndexOutOfBoundsException("adapterPosition $adapterPosition is not in range of items represented by adapter")
        }
        return sectionIndicesByAdapterPosition[adapterPosition]
    }

    /**
     * Given a sectionIndex and an adapter position get the local position of an item relative to the sectionIndex,
     * where the first item has position 0
     *
     * @param sectionIndex    the sectionIndex index
     * @param adapterPosition the adapter adapterPosition
     * @return the position relative to the sectionIndex of an item in that sectionIndex
     *
     *
     * Note, if the adapterPosition corresponds to a sectionIndex header, this will return -1
     */
    fun getPositionOfItemInSection(sectionIndex: Int, adapterPosition: Int): Int {
        if (sections == null) {
            buildSectionIndex()
        }
        if (sectionIndex < 0) {
            throw IndexOutOfBoundsException("sectionIndex $sectionIndex < 0")
        }
        if (sectionIndex >= sections!!.size) {
            throw IndexOutOfBoundsException("sectionIndex " + sectionIndex + " >= sections.size (" + sections!!.size + ")")
        }
        val section = sections!![sectionIndex]
        var localPosition = adapterPosition - section.adapterPosition
        if (localPosition > section.length) {
            throw IndexOutOfBoundsException("adapterPosition: " + adapterPosition + " is beyond sectionIndex: " + sectionIndex + " length: " + section.length)
        }
        if (section.hasHeader) {
            // adjust for header and ghostHeader
            localPosition -= 2
        }
        return localPosition
    }

    /**
     * Given a sectionIndex index, and an offset into the sectionIndex where 0 is the header, 1 is the ghostHeader, 2 is the first item in the sectionIndex, return the corresponding "global" adapter position
     *
     * @param sectionIndex      a sectionIndex index
     * @param offsetIntoSection offset into sectionIndex where 0 is the header, 1 is the first item, etc
     * @return the "global" adapter adapterPosition
     */
    private fun getAdapterPosition(sectionIndex: Int, offsetIntoSection: Int): Int {
        if (sections == null) {
            buildSectionIndex()
        }
        if (sectionIndex < 0) {
            throw IndexOutOfBoundsException("sectionIndex $sectionIndex < 0")
        }
        if (sectionIndex >= sections!!.size) {
            throw IndexOutOfBoundsException("sectionIndex " + sectionIndex + " >= sections.size (" + sections!!.size + ")")
        }
        val section = sections!![sectionIndex]
        val adapterPosition = section.adapterPosition
        return offsetIntoSection + adapterPosition
    }

    /**
     * Return the adapter position corresponding to the header of the provided section
     *
     * @param sectionIndex the index of the section
     * @return adapter position of that section's header, or NO_POSITION if section has no header
     */
    fun getAdapterPositionForSectionHeader(sectionIndex: Int): Int {
        return if (doesSectionHaveHeader(sectionIndex)) {
            getAdapterPosition(sectionIndex, 0)
        } else {
            NO_POSITION
        }
    }

    /**
     * Return the adapter position corresponding to the ghost header of the provided section
     *
     * @param sectionIndex the index of the section
     * @return adapter position of that section's ghost header, or NO_POSITION if section has no ghost header
     */
    fun getAdapterPositionForSectionGhostHeader(sectionIndex: Int): Int {
        return if (doesSectionHaveHeader(sectionIndex)) {
            getAdapterPosition(sectionIndex, 1) // ghost header follows the header
        } else {
            NO_POSITION
        }
    }

    /**
     * Return the adapter position corresponding to a specific item in the section
     *
     * @param sectionIndex      the index of the section
     * @param offsetIntoSection the offset of the item in the section where 0 would be the first item in the section
     * @return adapter position of the item in the section
     */
    fun getAdapterPositionForSectionItem(sectionIndex: Int, offsetIntoSection: Int): Int {
        return if (doesSectionHaveHeader(sectionIndex)) {
            getAdapterPosition(sectionIndex, offsetIntoSection) + 2 // header is at position 0, ghostHeader at position 1
        } else {
            getAdapterPosition(sectionIndex, offsetIntoSection)
        }
    }

    /**
     * Return the adapter position corresponding to the footer of the provided section
     *
     * @param sectionIndex the index of the section
     * @return adapter position of that section's footer, or NO_POSITION if section does not have footer
     */
    fun getAdapterPositionForSectionFooter(sectionIndex: Int): Int {
        return if (doesSectionHaveFooter(sectionIndex)) {
            val section = sections!![sectionIndex]
            val adapterPosition = section.adapterPosition
            adapterPosition + section.length - 1
        } else {
            NO_POSITION
        }
    }

    /**
     * Mark that a section is collapsed or not. By default sections are not collapsed and draw
     * all their child items. By "collapsing" a section, the child items are hidden.
     *
     * @param sectionIndex index of section
     * @param collapsed    if true, section is collapsed, false, it's open
     */
    fun setSectionIsCollapsed(sectionIndex: Int, collapsed: Boolean) {
        val notify = isSectionCollapsed(sectionIndex) != collapsed
        collapsedSections[sectionIndex] = collapsed
        if (notify) {
            if (sections == null) {
                buildSectionIndex()
            }
            val section = sections!![sectionIndex]
            val number = section.numberOfItems
            if (collapsed) {
                notifySectionItemRangeRemoved(sectionIndex, 0, number, false)
            } else {
                notifySectionItemRangeInserted(sectionIndex, 0, number, false)
            }
        }
    }

    /**
     * @param sectionIndex index of section
     * @return true if that section is collapsed
     */
    fun isSectionCollapsed(sectionIndex: Int): Boolean {
        return if (collapsedSections.containsKey(sectionIndex)) {
            collapsedSections[sectionIndex]!!
        } else
            false
    }

    private fun getSectionSelectionState(sectionIndex: Int): SectionSelectionState {
        var state = selectionStateBySection[sectionIndex]
        if (state != null) {
            return state
        }
        state = SectionSelectionState()
        selectionStateBySection[sectionIndex] = state
        return state
    }
    /**
     * Clear selection state
     *
     * @param notify if true, notifies data change for recyclerview, if false, silent
     */
    @JvmOverloads
    fun clearSelection(notify: Boolean = true) {

        val selectionState: HashMap<Int, SectionSelectionState?>? = if (notify) HashMap(selectionStateBySection) else null

        selectionStateBySection = HashMap()

        if (notify) {

            // walk the selection state and update the items which were selected
            for (sectionIndex in selectionState!!.keys) {
                val state = selectionState[sectionIndex]
                if (state!!.section) {
                    notifySectionDataSetChanged(sectionIndex)
                } else {
                    var i = 0
                    val s = state.items.size()
                    while (i < s) {
                        if (state.items.valueAt(i)) {
                            notifySectionItemChanged(sectionIndex, state.items.keyAt(i))
                        }
                        i++
                    }
                    if (state.footer) {
                        notifySectionFooterChanged(sectionIndex)
                    }
                }
            }
        }
    }

    /**
     * Quick check if selection is empty
     *
     * @return true iff the selection state is empty
     */
    val isSelectionEmpty: Boolean
        get() {
            for (sectionIndex in selectionStateBySection.keys) {
                val state = selectionStateBySection[sectionIndex]
                if (state!!.section) {
                    return false
                } else {
                    var i = 0
                    val s = state.items.size()
                    while (i < s) {
                        if (state.items.valueAt(i)) {
                            return false
                        }
                        i++
                    }
                    if (state.footer) {
                        return false
                    }
                }
            }
            return true
        }

    val selectedItemCount: Int
        get() {
            var count = 0
            for (sectionIndex in selectionStateBySection.keys) {
                val state = selectionStateBySection[sectionIndex]
                if (state!!.section) {
                    count += getNumberOfItemsInSection(sectionIndex)
                    if (doesSectionHaveFooter(sectionIndex)) {
                        count++
                    }
                } else {
                    var i = 0
                    val s = state.items.size()
                    while (i < s) {
                        val selected = state.items.valueAt(i)
                        if (selected) {
                            count++
                        }
                        i++
                    }
                    if (state.footer) {
                        count++
                    }
                }
            }
            return count
        }

    /**
     * Visitor interface for walking adapter selection state.
     */
    interface SelectionVisitor {
        fun onVisitSelectedSection(sectionIndex: Int)
        fun onVisitSelectedSectionItem(sectionIndex: Int, itemIndex: Int)
        fun onVisitSelectedFooter(sectionIndex: Int)
    }

    /**
     * Walks the selection state of the adapter, in reverse order from end to front. This is to ensure that any additions or deletions
     * which are made based on selection are safe to perform.
     *
     * @param visitor visitor which is invoked to process selection state
     */
    fun traverseSelection(visitor: SelectionVisitor) {

        // walk the section indices backwards
        val sectionIndices: List<Int> =
            ArrayList(selectionStateBySection.keys)
        Collections.sort(sectionIndices, Collections.reverseOrder())
        for (sectionIndex in sectionIndices) {
            val state = selectionStateBySection[sectionIndex] ?: continue
            if (state.section) {
                visitor.onVisitSelectedSection(sectionIndex)
            } else {
                if (state.footer) {
                    visitor.onVisitSelectedFooter(sectionIndex)
                }

                // walk items backwards
                for (i in state.items.size() - 1 downTo 0) {
                    if (state.items.valueAt(i)) {
                        visitor.onVisitSelectedSectionItem(sectionIndex, state.items.keyAt(i))
                    }
                }
            }
        }
    }

    /**
     * Set whether an entire section is selected. this affects ALL items (and footer) in section.
     *
     * @param sectionIndex index of the section
     * @param selected     selection state
     */
    fun setSectionSelected(sectionIndex: Int, selected: Boolean) {
        val state = getSectionSelectionState(sectionIndex)
        if (state.section != selected) {
            state.section = selected

            // update all items and footers
            state.items.clear()
            var i = 0
            val n = getNumberOfItemsInSection(sectionIndex)
            while (i < n) {
                state.items.put(i, selected)
                i++
            }
            if (doesSectionHaveFooter(sectionIndex)) {
                state.footer = selected
            }
            notifySectionDataSetChanged(sectionIndex)
        }
    }

    /**
     * Toggle selection state of an entire section
     *
     * @param sectionIndex index of section
     */
    fun toggleSectionSelected(sectionIndex: Int) {
        setSectionSelected(sectionIndex, !isSectionSelected(sectionIndex))
    }

    /**
     * Check if section is selected
     *
     * @param sectionIndex index of section
     * @return true if section is selected
     */
    fun isSectionSelected(sectionIndex: Int): Boolean {
        return getSectionSelectionState(sectionIndex).section
    }

    /**
     * Select a specific item in a section. Note, if the section is selected, this is a no-op.
     *
     * @param sectionIndex index of section
     * @param itemIndex    index of item, relative to section
     * @param selected     selection state
     */
    fun setSectionItemSelected(sectionIndex: Int, itemIndex: Int, selected: Boolean) {
        val state = getSectionSelectionState(sectionIndex)
        if (state.section) {
            return
        }
        if (selected != state.items[itemIndex]) {
            state.items.put(itemIndex, selected)
            notifySectionItemChanged(sectionIndex, itemIndex)
        }
    }

    /**
     * Toggle selection state of a specific item in a section
     *
     * @param sectionIndex index of section
     * @param itemIndex    index of item in section
     */
    fun toggleSectionItemSelected(sectionIndex: Int, itemIndex: Int) {
        setSectionItemSelected(
            sectionIndex,
            itemIndex,
            !isSectionItemSelected(sectionIndex, itemIndex)
        )
    }

    /**
     * Check whether a specific item in a section is selected, or if the entire section is selected
     *
     * @param sectionIndex index of section
     * @param itemIndex    index of item in section
     * @return true if the item is selected
     */
    fun isSectionItemSelected(sectionIndex: Int, itemIndex: Int): Boolean {
        val state = getSectionSelectionState(sectionIndex)
        return state.section || state.items[itemIndex]
    }

    /**
     * Select the footer of a section
     *
     * @param sectionIndex index of section
     * @param selected     selection state
     */
    fun setSectionFooterSelected(sectionIndex: Int, selected: Boolean) {
        val state = getSectionSelectionState(sectionIndex)
        if (state.section) {
            return
        }
        if (state.footer != selected) {
            state.footer = selected
            notifySectionFooterChanged(sectionIndex)
        }
    }

    /**
     * Toggle selection of footer in a section
     *
     * @param sectionIndex index of section
     */
    fun toggleSectionFooterSelection(sectionIndex: Int) {
        setSectionFooterSelected(sectionIndex, !isSectionFooterSelected(sectionIndex))
    }

    /**
     * Check whether footer of a section is selected, or if the entire section is selected
     *
     * @param sectionIndex section index
     * @return true if the footer is selected
     */
    fun isSectionFooterSelected(sectionIndex: Int): Boolean {
        val state = getSectionSelectionState(sectionIndex)
        return state.section || state.footer
    }

    /**
     * Notify that all data in the list is invalid and the entire list should be reloaded.
     * NOTE: This will clear selection state, and collapsed section state.
     * Equivalent to RecyclerView.Adapter.notifyDataSetChanged.
     * Never directly call notifyDataSetChanged.
     */
    fun notifyAllSectionsDataSetChanged() {
        buildSectionIndex()
        notifyDataSetChanged()
        collapsedSections.clear()
        selectionStateBySection.clear()
    }

    /**
     * Notify that all the items in a particular section are invalid and that section should be reloaded
     * Never directly call notifyDataSetChanged.
     * This will clear item selection state for the affected section.
     *
     * @param sectionIndex index of the section to reload.
     */
    fun notifySectionDataSetChanged(sectionIndex: Int) {
        if (sections == null) {
            buildSectionIndex()
            notifyAllSectionsDataSetChanged()
        } else {
            buildSectionIndex()
            val section =
                sections!![sectionIndex]
            notifyItemRangeChanged(section.adapterPosition, section.length)
        }

        // clear item selection state
        getSectionSelectionState(sectionIndex).items.clear()
    }

    /**
     * Notify that a range of items in a section has been inserted
     *
     * @param sectionIndex index of the section
     * @param fromPosition index to start adding
     * @param number       amount of items inserted
     */
    fun notifySectionItemRangeInserted(sectionIndex: Int, fromPosition: Int, number: Int) {
        notifySectionItemRangeInserted(sectionIndex, fromPosition, number, true)
    }

    private fun notifySectionItemRangeInserted(sectionIndex: Int, fromPosition: Int, number: Int, updateSelectionState: Boolean) {
        if (sections == null) {
            buildSectionIndex()
            notifyAllSectionsDataSetChanged()
        } else {
            buildSectionIndex()
            val section =
                sections!![sectionIndex]

            // 0 is a valid position to insert from
            if (fromPosition > section.numberOfItems) {
                throw IndexOutOfBoundsException("itemIndex adapterPosition: " + fromPosition + " exceeds sectionIndex numberOfItems: " + section.numberOfItems)
            }
            var offset = fromPosition
            if (section.hasHeader) {
                offset += 2
            }
            notifyItemRangeInserted(section.adapterPosition + offset, number)
        }
        if (updateSelectionState) {
            // update selection state by inserting unselected spaces
            updateSectionItemRangeSelectionState(sectionIndex, fromPosition, +number)
        }
    }

    /**
     * Notify that a range of items in a section has been removed
     *
     * @param sectionIndex index of the section
     * @param fromPosition index to start removing from
     * @param number       amount of items removed
     */
    fun notifySectionItemRangeRemoved(sectionIndex: Int, fromPosition: Int, number: Int) {
        notifySectionItemRangeRemoved(sectionIndex, fromPosition, number, true)
    }

    private fun notifySectionItemRangeRemoved(sectionIndex: Int, fromPosition: Int, number: Int, updateSelectionState: Boolean) {
        if (sections == null) {
            buildSectionIndex()
            notifyAllSectionsDataSetChanged()
        } else {
            val section =
                sections!![sectionIndex]

            // 0 is a valid position to remove from
            if (fromPosition > section.numberOfItems) {
                throw IndexOutOfBoundsException("itemIndex adapterPosition: " + fromPosition + " exceeds sectionIndex numberOfItems: " + section.numberOfItems)
            }

            // Verify we don't run off the end of the section
            if (fromPosition + number > section.numberOfItems) {
                throw IndexOutOfBoundsException("itemIndex adapterPosition: " + fromPosition + number + " exceeds sectionIndex numberOfItems: " + section.numberOfItems)
            }
            var offset = fromPosition
            if (section.hasHeader) {
                offset += 2
            }
            notifyItemRangeRemoved(section.adapterPosition + offset, number)
            buildSectionIndex()
        }
        if (updateSelectionState) {
            // update selection state by removing specified items
            updateSectionItemRangeSelectionState(sectionIndex, fromPosition, -number)
        }
    }

    /**
     * Notify that a particular itemIndex in a section has been invalidated and must be reloaded
     * Never directly call notifyItemChanged
     *
     * @param sectionIndex the index of the section containing the itemIndex
     * @param itemIndex    the index of the item relative to the section (where 0 is the first item in the section)
     */
    fun notifySectionItemChanged(sectionIndex: Int, itemIndex: Int) {
        var index = itemIndex
        if (sections == null) {
            buildSectionIndex()
            notifyAllSectionsDataSetChanged()
        } else {
            buildSectionIndex()
            val section =
                sections!![sectionIndex]
            if (index >= section.numberOfItems) {
                throw IndexOutOfBoundsException("itemIndex adapterPosition: " + index + " exceeds sectionIndex numberOfItems: " + section.numberOfItems)
            }
            if (section.hasHeader) {
                index += 2
            }
            notifyItemChanged(section.adapterPosition + index)
        }
    }

    /**
     * Notify that an item has been added to a section
     * Never directly call notifyItemInserted
     *
     * @param sectionIndex index of the section
     * @param itemIndex    index of the item where 0 is the first position in the section
     */
    fun notifySectionItemInserted(sectionIndex: Int, itemIndex: Int) {
        if (sections == null) {
            buildSectionIndex()
            notifyAllSectionsDataSetChanged()
        } else {
            buildSectionIndex()
            val section =
                sections!![sectionIndex]
            var offset = itemIndex
            if (section.hasHeader) {
                offset += 2
            }
            notifyItemInserted(section.adapterPosition + offset)
        }
        updateSectionItemRangeSelectionState(sectionIndex, itemIndex, 1)
    }

    /**
     * Notify that an item has been removed from a section
     * Never directly call notifyItemRemoved
     *
     * @param sectionIndex index of the section
     * @param itemIndex    index of the item in the section where 0 is the first position in the section
     */
    fun notifySectionItemRemoved(sectionIndex: Int, itemIndex: Int) {
        if (sections == null) {
            buildSectionIndex()
            notifyAllSectionsDataSetChanged()
        } else {
            buildSectionIndex()
            val section =
                sections!![sectionIndex]
            var offset = itemIndex
            if (section.hasHeader) {
                offset += 2
            }
            notifyItemRemoved(section.adapterPosition + offset)
        }
        updateSectionItemRangeSelectionState(sectionIndex, itemIndex, -1)
    }

    /**
     * Notify that a new section has been added
     *
     * @param sectionIndex position of the new section
     */
    fun notifySectionInserted(sectionIndex: Int) {
        if (sections == null) {
            buildSectionIndex()
            notifyAllSectionsDataSetChanged()
        } else {
            buildSectionIndex()
            val section =
                sections!![sectionIndex]
            notifyItemRangeInserted(section.adapterPosition, section.length)
        }
        updateCollapseAndSelectionStateForSectionChange(sectionIndex, +1)
    }

    /**
     * Notify that a section has been removed
     *
     * @param sectionIndex position of the removed section
     */
    fun notifySectionRemoved(sectionIndex: Int) {
        if (sections == null) {
            buildSectionIndex()
            notifyAllSectionsDataSetChanged()
        } else {
            val section =
                sections!![sectionIndex]
            buildSectionIndex()
            notifyItemRangeRemoved(section.adapterPosition, section.length)
        }
        updateCollapseAndSelectionStateForSectionChange(sectionIndex, -1)
    }

    /**
     * Notify that a section has had a footer added to it
     *
     * @param sectionIndex position of the section
     */
    fun notifySectionFooterInserted(sectionIndex: Int) {
        if (sections == null) {
            buildSectionIndex()
            notifyAllSectionsDataSetChanged()
        } else {
            buildSectionIndex()
            val section =
                sections!![sectionIndex]
            require(section.hasFooter) { "notifySectionFooterInserted: adapter implementation reports that section $sectionIndex does not have a footer" }
            notifyItemInserted(section.adapterPosition + section.length - 1)
        }
    }

    /**
     * Notify that a section has had a footer removed from it
     *
     * @param sectionIndex position of the section
     */
    fun notifySectionFooterRemoved(sectionIndex: Int) {
        if (sections == null) {
            buildSectionIndex()
            notifyAllSectionsDataSetChanged()
        } else {
            buildSectionIndex()
            val section =
                sections!![sectionIndex]
            require(!section.hasFooter) { "notifySectionFooterRemoved: adapter implementation reports that section $sectionIndex has a footer" }
            notifyItemRemoved(section.adapterPosition + section.length)
        }
    }

    /**
     * Notify that a section's footer's content has changed
     *
     * @param sectionIndex position of the section
     */
    fun notifySectionFooterChanged(sectionIndex: Int) {
        if (sections == null) {
            buildSectionIndex()
            notifyAllSectionsDataSetChanged()
        } else {
            buildSectionIndex()
            val section =
                sections!![sectionIndex]
            require(section.hasFooter) { "notifySectionFooterChanged: adapter implementation reports that section $sectionIndex does not have a footer" }
            notifyItemChanged(section.adapterPosition + section.length - 1)
        }
    }

    /**
     * Post an action to be run later.
     * RecyclerView doesn't like being mutated during a scroll. We can't detect when a
     * scroll is actually happening, unfortunately, so the best we can do is post actions
     * from notify* methods to be run at a later date.
     *
     * @param action action to run
     */
    private fun post(action: Runnable) {
        if (mainThreadHandler == null) {
            mainThreadHandler = Handler(Looper.getMainLooper())
        }
        mainThreadHandler!!.post(action)
    }

    private fun buildSectionIndex() {
        sections = ArrayList()
        var i = 0
        run {
            var s = 0
            val ns = getNumberOfSections()
            while (s < ns) {
                val section = Section()
                section.adapterPosition = i
                section.hasHeader = doesSectionHaveHeader(s)
                section.hasFooter = doesSectionHaveFooter(s)
                if (isSectionCollapsed(s)) {
                    section.length = 0
                    section.numberOfItems = getNumberOfItemsInSection(s)
                } else {
                    section.numberOfItems = getNumberOfItemsInSection(s)
                    section.length = section.numberOfItems
                }
                if (section.hasHeader) {
                    section.length += 2 // room for header and ghostHeader
                }
                if (section.hasFooter) {
                    section.length++
                }
                this.sections!!.add(section)
                i += section.length
                s++
            }
        }
        totalNumberOfItems = i
        i = 0
        sectionIndicesByAdapterPosition = IntArray(totalNumberOfItems)
        var s = 0
        val ns = getNumberOfSections()
        while (s < ns) {
            val section = sections!![s]
            for (p in 0 until section.length) {
                sectionIndicesByAdapterPosition[i + p] = s
            }
            i += section.length
            s++
        }
    }

    private fun updateSectionItemRangeSelectionState(sectionIndex: Int, fromPosition: Int, delta: Int) {
        val sectionSelectionState = getSectionSelectionState(sectionIndex)
        val itemState = sectionSelectionState.items.clone()
        sectionSelectionState.items.clear()
        var i = 0
        val n = itemState.size()
        while (i < n) {
            val pos = itemState.keyAt(i)
            if (delta < 0 && pos >= fromPosition && pos < fromPosition - delta) { // erasure
                i++
                continue
            }
            var newPos = pos
            if (pos >= fromPosition) {
                newPos += delta
            }
            if (itemState[pos]) {
                sectionSelectionState.items.put(newPos, true)
            }
            i++
        }
    }

    private fun updateCollapseAndSelectionStateForSectionChange(sectionIndex: Int, delta: Int) {

        // update section collapse state
        val collapseState =
            HashMap(collapsedSections)
        collapsedSections.clear()
        for (i in collapseState.keys) {
            // erasure
            if (delta < 0 && i == sectionIndex) {
                continue
            }
            var j = i
            if (j >= sectionIndex) {
                j += delta
            }
            collapsedSections[j] = collapseState[i]
        }

        // update selection state
        val selectionState =
            HashMap(selectionStateBySection)
        selectionStateBySection.clear()
        for (i in selectionState.keys) {
            // erasure
            if (delta < 0 && i == sectionIndex) {
                continue
            }
            var j = i
            if (j >= sectionIndex) {
                j += delta
            }
            selectionStateBySection[j] = selectionState[i]
        }
    }

    override fun getItemCount(): Int {
        if (sections == null) {
            buildSectionIndex()
        }
        return totalNumberOfItems
    }

    override fun getItemViewType(adapterPosition: Int): Int {
        if (sections == null) {
            buildSectionIndex()
        }
        if (adapterPosition < 0) {
            throw IndexOutOfBoundsException("adapterPosition ($adapterPosition) cannot be < 0")
        } else if (adapterPosition >= itemCount) {
            throw IndexOutOfBoundsException("adapterPosition ($adapterPosition)  cannot be > getItemCount() ($itemCount)")
        }
        val sectionIndex = getSectionForAdapterPosition(adapterPosition)
        val section = sections!![sectionIndex]
        var localPosition = adapterPosition - section.adapterPosition
        val baseType = getItemViewBaseType(section, localPosition)
        var userType = 0
        when (baseType) {
            TYPE_HEADER -> {
                userType = getSectionHeaderUserType(sectionIndex)
                require(!(userType < 0 || userType > 0xFF)) { "Custom header view type ($userType) must be in range [0,255]" }
            }
            TYPE_ITEM -> {
                // adjust local position to accommodate header & ghost header
                if (section.hasHeader) {
                    localPosition -= 2
                }
                userType = getSectionItemUserType(sectionIndex, localPosition)
                require(!(userType < 0 || userType > 0xFF)) { "Custom item view type ($userType) must be in range [0,255]" }
            }
            TYPE_FOOTER -> {
                userType = getSectionFooterUserType(sectionIndex)
                require(!(userType < 0 || userType > 0xFF)) { "Custom footer view type ($userType) must be in range [0,255]" }
            }
        }


        // base is bottom 8 bits, user type next 8 bits
        return userType and 0xFF shl 8 or (baseType and 0xFF)
    }

    /**
     * @param adapterPosition the adapterPosition of the item in question
     * @return the base type (TYPE_HEADER, TYPE_GHOST_HEADER, TYPE_ITEM, TYPE_FOOTER) of the item at a given adapter position
     */
    fun getItemViewBaseType(adapterPosition: Int): Int {
        return unmaskBaseViewType(getItemViewType(adapterPosition))
    }

    /**
     * @param adapterPosition the adapterPosition of the item in question
     * @return the custom user type of the item at the adapterPosition
     */
    fun getItemViewUserType(adapterPosition: Int): Int {
        return unmaskUserViewType(getItemViewType(adapterPosition))
    }

    fun getItemViewBaseType(section: Section, localPosition: Int): Int {
        return if (section.hasHeader && section.hasFooter) {
            if (localPosition == 0) {
                TYPE_HEADER
            } else if (localPosition == 1) {
                TYPE_GHOST_HEADER
            } else if (localPosition == section.length - 1) {
                TYPE_FOOTER
            } else {
                TYPE_ITEM
            }
        } else if (section.hasHeader) {
            if (localPosition == 0) {
                TYPE_HEADER
            } else if (localPosition == 1) {
                TYPE_GHOST_HEADER
            } else {
                TYPE_ITEM
            }
        } else if (section.hasFooter) {
            if (localPosition == section.length - 1) {
                TYPE_FOOTER
            } else {
                TYPE_ITEM
            }
        } else {
            // this sections has no header or footer
            TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val baseViewType = unmaskBaseViewType(viewType)
        val userViewType = unmaskUserViewType(viewType)
        when (baseViewType) {
            TYPE_ITEM -> return onCreateItemViewHolder(parent, userViewType) as ViewHolder
            TYPE_HEADER -> return onCreateHeaderViewHolder(parent, userViewType) as ViewHolder
            TYPE_FOOTER -> return onCreateFooterViewHolder(parent, userViewType) as ViewHolder
            TYPE_GHOST_HEADER -> return onCreateGhostHeaderViewHolder(parent)
        }
        throw IndexOutOfBoundsException("unrecognized viewType: $viewType does not correspond to TYPE_ITEM, TYPE_HEADER or TYPE_FOOTER")
    }

    override fun onBindViewHolder(holder: ViewHolder, adapterPosition: Int) {

        val section = getSectionForAdapterPosition(adapterPosition)

        // bind the sections to this view holder
        holder.section = section
        holder.numberOfItemsInSection = getNumberOfItemsInSection(section)

        // tag the viewHolder's item so as to make it possible to track in layout manager
        tagViewHolderItemView(holder, section, adapterPosition)
        val baseType = unmaskBaseViewType(holder.itemViewType)
        val userType = unmaskUserViewType(holder.itemViewType)
        when (baseType) {
            TYPE_HEADER -> onBindHeaderViewHolder(holder as HeaderViewHolder?, section, userType)
            TYPE_ITEM -> {
                val ivh = holder as ItemViewHolder?
                val positionInSection = getPositionOfItemInSection(section, adapterPosition)
                ivh?.positionInSection = positionInSection
                onBindItemViewHolder(ivh, section, positionInSection, userType)
            }
            TYPE_FOOTER -> onBindFooterViewHolder(
                holder as FooterViewHolder?,
                section,
                userType
            )
            TYPE_GHOST_HEADER -> onBindGhostHeaderViewHolder(
                holder as GhostHeaderViewHolder?,
                section
            )
            else -> throw IllegalArgumentException("unrecognized viewType: $baseType does not correspond to TYPE_ITEM, TYPE_HEADER, TYPE_GHOST_HEADER or TYPE_FOOTER")
        }
    }

    /**
     * Tag the itemView of the view holder with information needed for the layout to do its sticky positioning.
     * Specifically, it tags R.id.sectioning_adapter_tag_key_view_type to the item type, R.id.sectioning_adapter_tag_key_view_section
     * to the item's section, and R.id.sectioning_adapter_tag_key_view_adapter_position which is the adapter position of the view
     *
     * @param holder          the view holder containing the itemView to tag
     * @param section         the section index
     * @param adapterPosition the adapter position of the view holder
     */
    fun tagViewHolderItemView(holder: ViewHolder?, section: Int, adapterPosition: Int) {
        val view = holder!!.itemView
        view.setTag(sectioning_adapter_tag_key_view_viewholder, holder)
    }

    companion object {

        private const val TAG = "SectioningAdapter"

        const val NO_POSITION = -1
        const val TYPE_HEADER = 0
        const val TYPE_GHOST_HEADER = 1
        const val TYPE_ITEM = 2
        const val TYPE_FOOTER = 3

        fun unmaskBaseViewType(itemViewTypeMask: Int): Int {
            return itemViewTypeMask and 0xFF // base view type (HEADER/ITEM/FOOTER/GHOST_HEADER) is lower 8 bits
        }

        fun unmaskUserViewType(itemViewTypeMask: Int): Int {
            return itemViewTypeMask shr 8 and 0xFF // use type is in 0x0000FF00 segment
        }

    }

}



//*****************************************************************



/**
 * StickyHeaderLayoutManager
 * Provides equivalent behavior to a simple LinearLayoutManager, but where section header items
 * are positioned in a "sticky" manner like the section headers in iOS's UITableView.
 * StickyHeaderLayoutManager MUST be used in conjunction with SectioningAdapter.
 *
 * @see SectioningAdapter
 */

open class StickyHeaderLayoutManager : LinearLayoutManager {


    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    constructor(context: Context, orientation: Int, reverseLayout: Boolean) : super(context, orientation, reverseLayout)


    enum class HeaderPosition {
        NONE, NATURAL, STICKY, TRAILING
    }

    /**
     * Callback interface for monitoring when header positions change between members of HeaderPosition enum values.
     * This can be useful if client code wants to change appearance for headers in HeaderPosition.STICKY vs normal positioning.
     *
     * @see HeaderPosition
     */
    interface HeaderPositionChangedCallback {
        /**
         * Called when a sections header positioning approach changes. The position can be HeaderPosition.NONE, HeaderPosition.NATURAL, HeaderPosition.STICKY or HeaderPosition.TRAILING
         *
         * @param sectionIndex the sections [0...n)
         * @param header       the header view
         * @param oldPosition  the previous positioning of the header (NONE, NATURAL, STICKY or TRAILING)
         * @param newPosition  the new positioning of the header (NATURAL, STICKY or TRAILING)
         */
        fun onHeaderPositionChanged(
            sectionIndex: Int,
            header: View?,
            oldPosition: HeaderPosition?,
            newPosition: HeaderPosition?
        )
    }

    private var adapter: SectioningAdapter? = null

    // holds all the visible section headers
    private val headerViews = HashSet<View?>()

    // holds the HeaderPosition for each header
    private val headerPositionsBySection = HashMap<Int, HeaderPosition>()

    /**
     * Assign callback object to be notified when a header view position changes between states of the HeaderPosition enum
     *
     * @param headerPositionChangedCallback the callback
     * @see HeaderPosition
     */
    var headerPositionChangedCallback: HeaderPositionChangedCallback? = null

    // adapter position of first (lowest-y-value) visible item.
    private var firstViewAdapterPosition = 0

    // top of first (lowest-y-value) visible item.
    private var firstViewTop = 0

    // adapter position (iff >= 0) of the item selected in scrollToPosition
    private var scrollTargetAdapterPosition = -1
    private var pendingSavedState: SavedState? = null

    override fun onAdapterChanged(oldAdapter: RecyclerView.Adapter<*>?, newAdapter: RecyclerView.Adapter<*>?) {
        super.onAdapterChanged(oldAdapter, newAdapter)
        adapter = try {
            newAdapter as SectioningAdapter?
        } catch (e: ClassCastException) {
            throw ClassCastException("StickyHeaderLayoutManager must be used with a RecyclerView where the adapter is a kind of SectioningAdapter")
        }
        removeAllViews()
        headerViews.clear()
        headerPositionsBySection.clear()
    }

    override fun onAttachedToWindow(view: RecyclerView) {
        super.onAttachedToWindow(view)
        adapter = try {
            view.adapter as SectioningAdapter?
        } catch (e: ClassCastException) {
            throw ClassCastException("StickyHeaderLayoutManager must be used with a RecyclerView where the adapter is a kind of SectioningAdapter")
        }
    }

    override fun onDetachedFromWindow(view: RecyclerView, recycler: RecyclerView.Recycler) {
        super.onDetachedFromWindow(view, recycler)

        // Update positions in case we need to save post-detach
        updateFirstAdapterPosition()
    }

    override fun onSaveInstanceState(): Parcelable? {
        if (pendingSavedState != null) {
            return pendingSavedState
        }
        // Check if we're detached; if not, update
        if (adapter != null) updateFirstAdapterPosition()
        val state = SavedState()
        state.firstViewAdapterPosition = firstViewAdapterPosition
        state.firstViewTop = firstViewTop
        return state
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state == null) {
            return
        }
        if (state is SavedState) {
            pendingSavedState = state
            requestLayout()
        } else {
            Log.e(TAG, "onRestoreInstanceState: invalid saved state class, expected: " + SavedState::class.java.canonicalName + " got: " + state.javaClass.canonicalName)
        }
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        if (adapter == null) {
            return
        }
        if (adapter!!.itemCount == 0) {
            removeAndRecycleAllViews(recycler)
            return
        }
        if (scrollTargetAdapterPosition >= 0) {
            firstViewAdapterPosition = scrollTargetAdapterPosition
            firstViewTop = 0
            scrollTargetAdapterPosition = RecyclerView.NO_POSITION // we're done here
        } else if (pendingSavedState != null && pendingSavedState!!.isValid) {
            firstViewAdapterPosition = pendingSavedState!!.firstViewAdapterPosition
            firstViewTop = pendingSavedState!!.firstViewTop
            pendingSavedState = null // we're done with saved state now
        } else {
            updateFirstAdapterPosition()
        }
        var top = firstViewTop

        // RESET
        headerViews.clear()
        headerPositionsBySection.clear()
        detachAndScrapAttachedViews(recycler)
        var height: Int
        val left = paddingLeft
        val right = width - paddingRight
        val parentBottom = getHeight() - paddingBottom
        var totalVendedHeight = 0

        // If we emptied the view with a notify, we may overshoot and fail to draw
        if (firstViewAdapterPosition >= state.itemCount) {
            firstViewAdapterPosition = state.itemCount - 1
        }

        // walk through adapter starting at firstViewAdapterPosition stacking each vended item
        var adapterPosition = firstViewAdapterPosition
        while (adapterPosition < state.itemCount) {

            val v = recycler.getViewForPosition(adapterPosition)
            addView(v)
            measureChildWithMargins(v, 0, 0)

            when (getViewBaseType(v)) {
                SectioningAdapter.TYPE_HEADER -> {
                    headerViews.add(v)

                    // use the header's height
                    height = getDecoratedMeasuredHeight(v)
                    layoutDecorated(v, left, top, right, top + height)

                    // we need to vend the ghost header and position/size it same as the actual header
                    adapterPosition++
                    val ghostHeader = recycler.getViewForPosition(adapterPosition)
                    addView(ghostHeader)
                    layoutDecorated(ghostHeader, left, top, right, top + height)
                }

                SectioningAdapter.TYPE_GHOST_HEADER -> {
                    // we need to back up and get the header for this ghostHeader
                    val headerView = recycler.getViewForPosition(adapterPosition - 1)
                    headerViews.add(headerView)
                    addView(headerView)
                    measureChildWithMargins(headerView, 0, 0)
                    height = getDecoratedMeasuredHeight(headerView)
                    layoutDecorated(headerView, left, top, right, top + height)
                    layoutDecorated(v, left, top, right, top + height)
                }

                else -> {
                    height = getDecoratedMeasuredHeight(v)
                    layoutDecorated(v, left, top, right, top + height)
                }
            }

            top += height
            totalVendedHeight += height

            // if the item we just laid out falls off the bottom of the view, we're done
            if (v.bottom >= parentBottom) {
                break
            }
            adapterPosition++
        }

        // determine if scrolling is necessary to fill viewport
        val innerHeight = getHeight() - (paddingTop + paddingBottom)
        if (totalVendedHeight < innerHeight) {
            // note: we're passing null for RecyclerView.State - this is "safe"
            // only because we don't use it for scrolling negative dy
            scrollVerticallyBy(totalVendedHeight - innerHeight, recycler, null)
        } else {
            // no scroll correction necessary, so position headers
            updateHeaderPositions(recycler)
        }
    }

    /**
     * Get the header item for a given section, creating it if it's not already in the view hierarchy
     *
     * @param recycler     the recycler
     * @param sectionIndex the index of the section for in question
     * @return the header, or null if the adapter specifies no header for the section
     */
    private fun createSectionHeaderIfNeeded(recycler: RecyclerView.Recycler, sectionIndex: Int): View? {
        return if (adapter!!.doesSectionHaveHeader(sectionIndex)) {

            // first, see if we've already got a header for this section
            var i = 0
            val n = childCount
            while (i < n) {
                val view = getChildAt(i)
                if (getViewBaseType(view) == SectioningAdapter.TYPE_HEADER && getViewSectionIndex(view) == sectionIndex) {
                    return view
                }
                i++
            }

            // looks like we need to create one
            val headerAdapterPosition = adapter!!.getAdapterPositionForSectionHeader(sectionIndex)
            val headerView = recycler.getViewForPosition(headerAdapterPosition)
            headerViews.add(headerView)
            addView(headerView)
            measureChildWithMargins(headerView, 0, 0)
            headerView
        } else {
            throw IllegalStateException("createSectionHeaderIfNeeded should not be called for a section which does not have a header")
        }
    }

    override fun scrollVerticallyBy(dy: Int, recycler: RecyclerView.Recycler, state: RecyclerView.State?): Int {

        //Log.i(TAG, "scrollVerticallyBy: dy: " + dy + " getChildCount: " + getChildCount() + " adapter count: " + adapter.getItemCount());
        if (childCount == 0) {
            return 0
        }
        var scrolled = 0
        val left = paddingLeft
        val right = width - paddingRight
        if (dy < 0) {

            // content moving downwards, so we're panning to top of list
            var topView: View? = topmostChildView ?: return 0
            while (scrolled > dy) {

                // get the topmost view
                val hangingTop = max(-getDecoratedTop(topView!!), 0)
                val scrollBy = min(scrolled - dy, hangingTop) // scrollBy is positive, causing content to move downwards
                scrolled -= scrollBy
                offsetChildrenVertical(scrollBy)

                // vend next view above topView
                if (firstViewAdapterPosition > 0 && scrolled > dy) {
                    firstViewAdapterPosition--

                    // we're skipping headers. they should already be vended, but if we're vending a ghostHeader
                    // here an actual header will be vended if needed for measurement
                    var itemViewType = adapter!!.getItemViewBaseType(firstViewAdapterPosition)
                    var isHeader = itemViewType == SectioningAdapter.TYPE_HEADER

                    // skip the header, move to next item above
                    if (isHeader) {
                        firstViewAdapterPosition--
                        if (firstViewAdapterPosition < 0) {
                            break
                        }
                        itemViewType = adapter!!.getItemViewBaseType(firstViewAdapterPosition)
                        isHeader = itemViewType == SectioningAdapter.TYPE_HEADER

                        // If it's still a header, we don't need to do anything right now
                        if (isHeader) break
                    }
                    val v = recycler.getViewForPosition(firstViewAdapterPosition)
                    addView(v, 0)
                    val bottom = getDecoratedTop(topView)
                    var top: Int
                    val isGhostHeader = itemViewType == SectioningAdapter.TYPE_GHOST_HEADER
                    top = if (isGhostHeader) {
                        val header = createSectionHeaderIfNeeded(recycler, adapter!!.getSectionForAdapterPosition(firstViewAdapterPosition))
                        bottom - getDecoratedMeasuredHeight(header!!) // header is already measured
                    } else {
                        measureChildWithMargins(v, 0, 0)
                        bottom - getDecoratedMeasuredHeight(v)
                    }
                    layoutDecorated(v, left, top, right, bottom)
                    topView = v
                } else {
                    break
                }
            }

        } else {

            // content moving up, we're headed to bottom of list
            val parentHeight = height
            var bottomView = bottommostChildView ?: return 0
            while (scrolled < dy) {
                val hangingBottom = max(getDecoratedBottom(bottomView) - parentHeight, 0)
                val scrollBy = -min(dy - scrolled, hangingBottom)
                scrolled -= scrollBy
                offsetChildrenVertical(scrollBy)
                val adapterPosition = getViewAdapterPosition(bottomView)
                var nextAdapterPosition = adapterPosition + 1
                bottomView = if (scrolled < dy && nextAdapterPosition < state?.itemCount?:0) {
                    val top = getDecoratedBottom(bottomView)
                    when (adapter!!.getItemViewBaseType(nextAdapterPosition)) {
                        SectioningAdapter.TYPE_HEADER -> {

                            // get the header and measure it so we can followup immediately by vending the ghost header
                            val headerView = createSectionHeaderIfNeeded(recycler, adapter!!.getSectionForAdapterPosition(nextAdapterPosition))
                            val height = getDecoratedMeasuredHeight(headerView!!)
                            layoutDecorated(headerView, left, 0, right, height)

                            // but we need to vend the followup ghost header too
                            nextAdapterPosition++
                            val ghostHeader = recycler.getViewForPosition(nextAdapterPosition)
                            addView(ghostHeader)
                            layoutDecorated(ghostHeader, left, top, right, top + height)
                            ghostHeader
                        }
                        SectioningAdapter.TYPE_GHOST_HEADER -> {

                            // get the header and measure it so we can followup immediately by vending the ghost header
                            val headerView = createSectionHeaderIfNeeded(recycler, adapter!!.getSectionForAdapterPosition(nextAdapterPosition))
                            val height = getDecoratedMeasuredHeight(headerView!!)
                            layoutDecorated(headerView, left, 0, right, height)

                            // but we need to vend the followup ghost header too
                            val ghostHeader = recycler.getViewForPosition(nextAdapterPosition)
                            addView(ghostHeader)
                            layoutDecorated(ghostHeader, left, top, right, top + height)
                            ghostHeader
                        }
                        else -> {
                            val v = recycler.getViewForPosition(nextAdapterPosition)
                            addView(v)
                            measureChildWithMargins(v, 0, 0)
                            val height = getDecoratedMeasuredHeight(v)
                            layoutDecorated(v, left, top, right, top + height)
                            v
                        }
                    }
                } else {
                    break
                }
            }
        }
        val topmostView = topmostChildView
        if (topmostView != null) {
            firstViewTop = getDecoratedTop(topmostView)
        }
        updateHeaderPositions(recycler)
        recycleViewsOutOfBounds(recycler)
        return scrolled
    }

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun canScrollVertically(): Boolean {
        return true
    }

    override fun scrollToPosition(position: Int) {
        if (position < 0 || position > itemCount) {
            throw IndexOutOfBoundsException("adapter position out of range")
        }
        scrollTargetAdapterPosition = position
        pendingSavedState = null
        requestLayout()
    }

    /**
     * @param fullyVisibleOnly if true, the search will be limited to the first item not hanging off top of screen or partially obscured by a header
     * @return the viewholder for the first visible item (not header or footer)
     */
    fun getFirstVisibleItemViewHolder(fullyVisibleOnly: Boolean): SectioningAdapter.ItemViewHolder? {
        return getFirstVisibleViewHolderOfType(
            SectioningAdapter.TYPE_ITEM,
            fullyVisibleOnly
        ) as SectioningAdapter.ItemViewHolder?
    }

    /**
     * @param fullyVisibleOnly if true, the search will be limited to the first header not hanging off top of screen
     * @return the viewholder for the first visible header (not item or footer)
     */
    fun getFirstVisibleHeaderViewHolder(fullyVisibleOnly: Boolean): SectioningAdapter.HeaderViewHolder? {
        return getFirstVisibleViewHolderOfType(
            SectioningAdapter.TYPE_HEADER,
            fullyVisibleOnly
        ) as SectioningAdapter.HeaderViewHolder?
    }

    /**
     * @param fullyVisibleOnly if true, the search will be limited to the first footer not hanging off top of screen or partially obscured by a header
     * @return the viewholder for the first visible footer (not header or item)
     */
    fun getFirstVisibleFooterViewHolder(fullyVisibleOnly: Boolean): SectioningAdapter.FooterViewHolder? {
        return getFirstVisibleViewHolderOfType(
            SectioningAdapter.TYPE_FOOTER,
            fullyVisibleOnly
        ) as SectioningAdapter.FooterViewHolder?
    }

    private fun getFirstVisibleViewHolderOfType(baseType: Int, fullyVisibleOnly: Boolean): SectioningAdapter.ViewHolder? {
        if (childCount == 0) {
            return null
        }

        // we need to discard items which are obscured by a header, so find
        // how tall the first header is, and we'll filter that the decoratedTop of
        // our items is below this value
        var firstHeaderBottom = 0
        if (baseType != SectioningAdapter.TYPE_HEADER) {
            val firstHeader = getFirstVisibleHeaderViewHolder(false)
            if (firstHeader != null) {
                firstHeaderBottom = getDecoratedBottom(firstHeader.itemView)
            }
        }

        // note: We can't use child view order because we muck with moving things to front
        var topmostView: View? = null
        var top = Int.MAX_VALUE
        var i = 0
        val e = childCount
        while (i < e) {
            val v = getChildAt(i)

            // ignore views which are being deleted
            if (getViewAdapterPosition(v) == RecyclerView.NO_POSITION) {
                i++
                continue
            }

            // filter for desired type
            if (getViewBaseType(v) != baseType) {
                i++
                continue
            }

            // filter out items which are partially or fully obscured by a header
            val t = getDecoratedTop(v!!)
            val b = getDecoratedBottom(v)
            if (fullyVisibleOnly) {
                if (t < firstHeaderBottom) {
                    i++
                    continue
                }
            } else {
                if (b <= firstHeaderBottom + 1) {
                    i++
                    continue
                }
            }
            if (t < top) {
                top = t
                topmostView = v
            }
            i++
        }
        return topmostView?.let { getViewViewHolder(it) }
    }

    /**
     * @param fullyVisibleOnly if true, the search will be limited to the last item not hanging off bottom of screen
     * @return the viewholder for the last visible item (not header or footer)
     */
    fun getLastVisibleItemViewHolder(fullyVisibleOnly: Boolean): SectioningAdapter.ItemViewHolder? {
        return getLastVisibleViewHolderOfType(
            SectioningAdapter.TYPE_ITEM,
            fullyVisibleOnly
        ) as SectioningAdapter.ItemViewHolder?
    }

    /**
     * @param fullyVisibleOnly if true, the search will be limited to the last header not hanging off bottom of screen
     * @return the viewholder for the last visible header (not item or footer)
     */
    fun getLastVisibleHeaderViewHolder(fullyVisibleOnly: Boolean): SectioningAdapter.HeaderViewHolder? {
        return getLastVisibleViewHolderOfType(
            SectioningAdapter.TYPE_HEADER,
            fullyVisibleOnly
        ) as SectioningAdapter.HeaderViewHolder?
    }

    /**
     * @param fullyVisibleOnly if true, the search will be limited to the last footer not hanging off bottom of screen
     * @return the viewholder for the last visible footer (not header or item)
     */
    fun getLastVisibleFooterViewHolder(fullyVisibleOnly: Boolean): SectioningAdapter.FooterViewHolder? {
        return getLastVisibleViewHolderOfType(
            SectioningAdapter.TYPE_FOOTER,
            fullyVisibleOnly
        ) as SectioningAdapter.FooterViewHolder?
    }

    private fun getLastVisibleViewHolderOfType(
        baseType: Int,
        fullyVisibleOnly: Boolean
    ): SectioningAdapter.ViewHolder? {
        if (childCount == 0) {
            return null
        }
        val height = height

        // note: We can't use child view order because we muck with moving things to front
        var bottommostView: View? = null
        var bottom = 0
        var i = 0
        val e = childCount
        while (i < e) {
            val v = getChildAt(i)

            // ignore views which are being deleted
            if (getViewAdapterPosition(v) == RecyclerView.NO_POSITION) {
                i++
                continue
            }

            // filter for desired type
            if (getViewBaseType(v) != baseType) {
                i++
                continue
            }

            // filter out items which are partially or fully obscured
            val t = getDecoratedTop(v!!)
            val b = getDecoratedBottom(v)
            if (fullyVisibleOnly) {
                if (b < height) {
                    i++
                    continue
                }
            } else {
                if (t >= height) {
                    i++
                    continue
                }
            }
            if (b > bottom) {
                bottom = b
                bottommostView = v
            }
            i++
        }
        return bottommostView?.let { getViewViewHolder(it) }
    }

    override fun smoothScrollToPosition(recyclerView: RecyclerView, state: RecyclerView.State, position: Int) {
        if (position < 0 || position > itemCount) {
            throw IndexOutOfBoundsException("adapter position out of range")
        }
        pendingSavedState = null

        // see: https://blog.stylingandroid.com/scrolling-recyclerview-part-3/
        val firstVisibleChild = recyclerView.getChildAt(0)
        val itemHeight = getEstimatedItemHeightForSmoothScroll(recyclerView)
        val currentPosition = recyclerView.getChildAdapterPosition(firstVisibleChild)
        var distanceInPixels = abs((currentPosition - position) * itemHeight)
        if (distanceInPixels == 0) {
            distanceInPixels = abs(firstVisibleChild.y).toInt()
        }
        val context = recyclerView.context
        val scroller = SmoothScroller(context, distanceInPixels)
        scroller.targetPosition = position
        startSmoothScroll(scroller)
    }

    private fun getEstimatedItemHeightForSmoothScroll(recyclerView: RecyclerView): Int {
        var height = 0
        var i = 0
        val n = recyclerView.childCount
        while (i < n) {
            height =
                max(getDecoratedMeasuredHeight(recyclerView.getChildAt(i)), height)
            i++
        }
        return height
    }

    fun computeScrollVectorForPosition2(targetPosition: Int): Float {
        updateFirstAdapterPosition()
        if (targetPosition > firstViewAdapterPosition) {
            return 1f
        } else if (targetPosition < firstViewAdapterPosition) {
            return -1f
        }
        return 0f
    }

    private fun recycleViewsOutOfBounds(recycler: RecyclerView.Recycler) {
        val height = height
        val numChildren = childCount
        val remainingSections: MutableSet<Int> = HashSet()
        val viewsToRecycle: MutableSet<View?> =
            HashSet()

        // we do this in two passes.
        // first, recycle everything but headers
        for (i in 0 until numChildren) {
            val view = getChildAt(i)

            // skip views which have been recycled but are still in place because of animation
            if (isViewRecycled(view)) {
                continue
            }
            if (getViewBaseType(view) != SectioningAdapter.TYPE_HEADER) {
                if (getDecoratedBottom(view!!) < 0 || getDecoratedTop(view) > height) {
                    viewsToRecycle.add(view)
                } else {
                    // this view is visible, therefore the section lives
                    remainingSections.add(getViewSectionIndex(view))
                }
            }
        }

        // second pass, for each "orphaned" header (a header who's section is completely recycled)
        // we remove it if it's gone offscreen
        for (i in 0 until numChildren) {
            val view = getChildAt(i)

            // skip views which have been recycled but are still in place because of animation
            if (isViewRecycled(view)) {
                continue
            }
            val sectionIndex = getViewSectionIndex(view)
            if (getViewBaseType(view) == SectioningAdapter.TYPE_HEADER && !remainingSections.contains(sectionIndex)) {
                val translationY = view!!.translationY
                if (getDecoratedBottom(view) + translationY < 0 || getDecoratedTop(view) + translationY > height) {
                    viewsToRecycle.add(view)
                    headerViews.remove(view)
                    headerPositionsBySection.remove(sectionIndex)
                }
            }
        }
        for (view in viewsToRecycle) {
            removeAndRecycleView(view!!, recycler)
        }


        // determine the adapter adapterPosition of first visible item
        updateFirstAdapterPosition()
    }// ignore views which are being deleted

    // ignore headers

    // note: We can't use child view order because we muck with moving things to front
    private val topmostChildView: View? get() {
            if (childCount == 0) {
                return null
            }

            // note: We can't use child view order because we muck with moving things to front
            var topmostView: View? = null
            var top = Int.MAX_VALUE
            var i = 0
            val e = childCount
            while (i < e) {
                val v = getChildAt(i)

                // ignore views which are being deleted
                if (getViewAdapterPosition(v) == RecyclerView.NO_POSITION) {
                    i++
                    continue
                }

                // ignore headers
                if (getViewBaseType(v) == SectioningAdapter.TYPE_HEADER) {
                    i++
                    continue
                }
                val t = getDecoratedTop(v!!)
                if (t < top) {
                    top = t
                    topmostView = v
                }
                i++
            }
            return topmostView
        }// ignore views which are being deleted

    // ignore headers

    // note: We can't use child view order because we muck with moving things to front
    val bottommostChildView: View? get() {
            if (childCount == 0) {
                return null
            }

            // note: We can't use child view order because we muck with moving things to front
            var bottommostView: View? = null
            var bottom = Int.MIN_VALUE
            var i = 0
            val e = childCount
            while (i < e) {
                val v = getChildAt(i)

                // ignore views which are being deleted
                if (getViewAdapterPosition(v) == RecyclerView.NO_POSITION) {
                    i++
                    continue
                }

                // ignore headers
                if (getViewBaseType(v) == SectioningAdapter.TYPE_HEADER) {
                    i++
                    continue
                }
                val b = getDecoratedBottom(v!!)
                if (b > bottom) {
                    bottom = b
                    bottommostView = v
                }
                i++
            }
            return bottommostView
        }

    /**
     * Updates firstViewAdapterPosition to the adapter position  of the highest item in the list - e.g., the
     * adapter position of the item with lowest y value in the list
     *
     * @return the y value of the topmost view in the layout, or paddingTop if empty
     */
    private fun updateFirstAdapterPosition(): Int {

        // we're empty
        if (childCount == 0) {
            firstViewAdapterPosition = 0
            firstViewTop = paddingTop
            return firstViewTop
        }
        val topmostView = topmostChildView
        if (topmostView != null) {
            firstViewAdapterPosition = getViewAdapterPosition(topmostView)
            firstViewTop = min(topmostView.top, paddingTop)
            return firstViewTop
        }

        // as far as I can tell, if notifyDataSetChanged is called, onLayoutChildren
        // will be called, but all views will be marked as having NO_POSITION for
        // adapterPosition, which means the above approach of finding the topmostChildView
        // doesn't work. So, basically, leave firstViewAdapterPosition and firstViewTop
        // alone.
        return firstViewTop
    }

    private fun updateHeaderPositions(recycler: RecyclerView.Recycler) {

        // first, for each section represented by the current list of items,
        // ensure that the header for that section is extant
        val visitedSections: MutableSet<Int> = HashSet()
        var i = 0
        val n = childCount
        while (i < n) {
            val view = getChildAt(i)
            val sectionIndex = getViewSectionIndex(view)
            if (visitedSections.add(sectionIndex)) {
                if (adapter!!.doesSectionHaveHeader(sectionIndex)) {
                    createSectionHeaderIfNeeded(recycler, sectionIndex)
                }
            }
            i++
        }

        // header is always positioned at top
        val left = paddingLeft
        val right = width - paddingRight
        for (headerView in headerViews) {
            val sectionIndex = getViewSectionIndex(headerView)

            // find first and last non-header views in this section
            var ghostHeader: View? = null
            var firstViewInNextSection: View? = null
            var index = 0
            val childCount = childCount
            while (index < childCount) {
                val view = getChildAt(index)
                // the view has been recycled
                if (isViewRecycled(view)) {
                    index++
                    continue
                }
                val type = getViewBaseType(view)
                if (type == SectioningAdapter.TYPE_HEADER) {
                    index++
                    continue
                }
                val viewSectionIndex = getViewSectionIndex(view)
                if (viewSectionIndex == sectionIndex) {
                    if (type == SectioningAdapter.TYPE_GHOST_HEADER) {
                        ghostHeader = view
                    }
                } else if (viewSectionIndex == sectionIndex + 1) {
                    if (firstViewInNextSection == null) {
                        firstViewInNextSection = view
                    }
                }
                index++
            }
            val height = getDecoratedMeasuredHeight(headerView!!)
            var top = paddingTop

            // initial position mark
            var headerPosition = HeaderPosition.STICKY
            if (ghostHeader != null) {
                val ghostHeaderTop = getDecoratedTop(ghostHeader)
                if (ghostHeaderTop >= top) {
                    top = ghostHeaderTop
                    headerPosition = HeaderPosition.NATURAL
                }
            }
            if (firstViewInNextSection != null) {
                val nextViewTop = getDecoratedTop(firstViewInNextSection)
                if (nextViewTop - height < top) {
                    top = nextViewTop - height
                    headerPosition = HeaderPosition.TRAILING
                }
            }

            // now bring header to front of stack for overlap, and position it
            headerView.bringToFront()
            layoutDecorated(headerView, left, top, right, top + height)

            // notify adapter of positioning for this header
            recordHeaderPositionAndNotify(sectionIndex, headerView, headerPosition)
        }
    }

    private fun recordHeaderPositionAndNotify(sectionIndex: Int, headerView: View?, newHeaderPosition: HeaderPosition) {
        if (headerPositionsBySection.containsKey(sectionIndex)) {
            val currentHeaderPosition = headerPositionsBySection[sectionIndex]
            if (currentHeaderPosition != newHeaderPosition) {
                headerPositionsBySection[sectionIndex] = newHeaderPosition
                if (headerPositionChangedCallback != null) {
                    headerPositionChangedCallback!!.onHeaderPositionChanged(
                        sectionIndex,
                        headerView,
                        currentHeaderPosition,
                        newHeaderPosition
                    )
                }
            }
        } else {
            headerPositionsBySection[sectionIndex] = newHeaderPosition
            if (headerPositionChangedCallback != null) {
                headerPositionChangedCallback!!.onHeaderPositionChanged(
                    sectionIndex,
                    headerView,
                    HeaderPosition.NONE,
                    newHeaderPosition
                )
            }
        }
    }

    private fun isViewRecycled(view: View?): Boolean {
        return getViewAdapterPosition(view) == RecyclerView.NO_POSITION
    }

    private fun getViewBaseType(view: View?): Int {
        val adapterPosition = getViewAdapterPosition(view)
        return adapter!!.getItemViewBaseType(adapterPosition)
    }

    private fun getViewSectionIndex(view: View?): Int {
        val adapterPosition = getViewAdapterPosition(view)
        return adapter!!.getSectionForAdapterPosition(adapterPosition)
    }

    private fun getViewViewHolder(view: View?): SectioningAdapter.ViewHolder {
        return view!!.getTag(sectioning_adapter_tag_key_view_viewholder) as SectioningAdapter.ViewHolder
    }

    fun getViewAdapterPosition(view: View?): Int {
        return getViewViewHolder(view).adapterPosition
    }

    // https://blog.stylingandroid.com/scrolling-recyclerview-part-3/
    private inner class SmoothScroller internal constructor(context: Context, distanceInPixels: Int) : LinearSmoothScroller(context) {

        private val distanceInPixels: Float = distanceInPixels.toFloat()

        private val duration: Float

        override fun computeScrollVectorForPosition(targetPosition: Int): PointF? {
            return PointF(0f, this@StickyHeaderLayoutManager.computeScrollVectorForPosition2(targetPosition))
        }

        override fun calculateTimeForScrolling(dx: Int): Int {
            val proportion = dx.toFloat() / distanceInPixels
            return (duration * proportion).toInt()
        }

        init {
            val millisecondsPerPx = calculateSpeedPerPixel(context.resources.displayMetrics)
            duration =
                if (distanceInPixels < TARGET_SEEK_SCROLL_DISTANCE_PX) abs(distanceInPixels) * millisecondsPerPx
                else Companion.DEFAULT_DURATION
        }

    }

    private class SavedState : Parcelable {
        var firstViewAdapterPosition = RecyclerView.NO_POSITION
        var firstViewTop = 0

        internal constructor() {}
        internal constructor(`in`: Parcel) {
            firstViewAdapterPosition = `in`.readInt()
            firstViewTop = `in`.readInt()
        }

        constructor(other: SavedState) {
            firstViewAdapterPosition = other.firstViewAdapterPosition
            firstViewTop = other.firstViewTop
        }

        val isValid: Boolean
            get() = firstViewAdapterPosition >= 0

        fun invalidate() {
            firstViewAdapterPosition = RecyclerView.NO_POSITION
        }

        override fun toString(): String {
            return "<" + this.javaClass.canonicalName + " firstViewAdapterPosition: " + firstViewAdapterPosition + " firstViewTop: " + firstViewTop + ">"
        }

        override fun describeContents(): Int {
            return 0
        }

        override fun writeToParcel(dest: Parcel, flags: Int) {
            dest.writeInt(firstViewAdapterPosition)
            dest.writeInt(firstViewTop)
        }

        companion object {
            val CREATOR: Parcelable.Creator<SavedState?> = object : Parcelable.Creator<SavedState?> {
                override fun createFromParcel(`in`: Parcel): SavedState? {
                    return SavedState(`in`)
                }
                override fun newArray(size: Int): Array<SavedState?> {
                    return arrayOfNulls(
                        size
                    )
                }
            }
        }

    }

    companion object {

        private val TAG = StickyHeaderLayoutManager::class.java.simpleName

        private const val TARGET_SEEK_SCROLL_DISTANCE_PX = 10000
        private const val DEFAULT_DURATION = 1000f

    }

}



//*****************************************************************



abstract class PagedLoadScrollListener @JvmOverloads constructor(
    private val layoutManager: StickyHeaderLayoutManager,
    private val visibleThreshold: Int = DEFAULT_VISIBLE_THRESHOLD
) : RecyclerView.OnScrollListener() {

    interface LoadCompleteNotifier {
        /**
         * Call to notify that a load has completed, with new items present.
         */
        fun notifyLoadComplete()

        /**
         * Call to notify that a load has completed, but no new items were present, and none will be forthcoming.
         */
        fun notifyLoadExhausted()
    }

    private var currentPage = 0
    private var previousTotalItemCount = 0
    private var loading = false
    private var loadExhausted = false
    private val loadCompleteNotifier: LoadCompleteNotifier = object : LoadCompleteNotifier {
        override fun notifyLoadComplete() {
            loading = false
            previousTotalItemCount = layoutManager.itemCount
        }
        override fun notifyLoadExhausted() {
            loadExhausted = true
        }
    }

    override fun onScrolled(view: RecyclerView, dx: Int, dy: Int) {

        // no-op if we're loading, or exhausted
        if (loading || loadExhausted) {
            return
        }

        // If the total item count is zero and the previous isn't, assume the
        // list is invalidated and should be reset back to initial state
        val totalItemCount = layoutManager.itemCount
        if (totalItemCount < previousTotalItemCount) {
            currentPage = 0
            previousTotalItemCount = totalItemCount
        } else if (totalItemCount > 0) {
            val lastVisibleItem = layoutManager.bottommostChildView
            val lastVisibleItemAdapterPosition =
                layoutManager.getViewAdapterPosition(lastVisibleItem)
            if (lastVisibleItemAdapterPosition + visibleThreshold > totalItemCount) {
                currentPage++
                loading = true
                view.post { onLoadMore(currentPage, loadCompleteNotifier) }
            }
        }
    }

    fun resetPaging() {
        currentPage = 0
        previousTotalItemCount = 0
        loading = false
        loadExhausted = false
    }

    /**
     * Override this to handle loading of new data. Each time new data is pulled in, the page counter will increase by one.
     * When your load is complete, call the appropriate method on the loadComplete callback.
     * @param page the page counter. Increases by one each time onLoadMore is called.
     * @param loadComplete callback to invoke when your load has completed.
     */
    abstract fun onLoadMore(page: Int, loadComplete: LoadCompleteNotifier?)

    companion object {
        private val TAG = PagedLoadScrollListener::class.java.simpleName
        private const val DEFAULT_VISIBLE_THRESHOLD = 5
    }

}

