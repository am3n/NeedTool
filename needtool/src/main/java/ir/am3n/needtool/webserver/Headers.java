// 
// Decompiled by Procyon v0.5.36
// 

package ir.am3n.needtool.webserver;

import java.util.Collection;
import java.util.Set;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Headers implements Map<String, List<String>>
{
    HashMap<String, List<String>> map;
    
    public Headers() {
        this.map = new HashMap<String, List<String>>(32);
    }
    
    private String normalize(final String s) {
        if (s == null) {
            return null;
        }
        final int length = s.length();
        if (length == 0) {
            return s;
        }
        final char[] array = new char[length];
        final char[] charArray = s.toCharArray();
        if (charArray[0] >= 'a' && charArray[0] <= 'z') {
            charArray[0] -= ' ';
        }
        for (int i = 1; i < length; ++i) {
            if (charArray[i] >= 'A' && charArray[i] <= 'Z') {
                charArray[i] += ' ';
            }
        }
        return new String(charArray);
    }
    
    public int size() {
        return this.map.size();
    }
    
    public boolean isEmpty() {
        return this.map.isEmpty();
    }
    
    public boolean containsKey(final Object o) {
        return o != null && o instanceof String && this.map.containsKey(this.normalize((String)o));
    }
    
    public boolean containsValue(final Object value) {
        return this.map.containsValue(value);
    }
    
    public List<String> get(final Object o) {
        return this.map.get(this.normalize((String)o));
    }
    
    public String getFirst(final String s) {
        final List<String> list = this.map.get(this.normalize(s));
        if (list == null) {
            return null;
        }
        return list.get(0);
    }
    
    public List<String> put(final String s, final List<String> value) {
        return this.map.put(this.normalize(s), value);
    }
    
    public void add(final String s, final String s2) {
        final String normalize = this.normalize(s);
        List<String> value = this.map.get(normalize);
        if (value == null) {
            value = new LinkedList<String>();
            this.map.put(normalize, value);
        }
        value.add(s2);
    }
    
    public void set(final String s, final String e) {
        final LinkedList<String> list = new LinkedList<String>();
        list.add(e);
        this.put(s, (List<String>)list);
    }
    
    public List<String> remove(final Object o) {
        return this.map.remove(this.normalize((String)o));
    }
    
    public void putAll(final Map<? extends String, ? extends List<String>> m) {
        this.map.putAll(m);
    }
    
    public void clear() {
        this.map.clear();
    }
    
    public Set<String> keySet() {
        return this.map.keySet();
    }
    
    public Collection<List<String>> values() {
        return this.map.values();
    }
    
    public Set<Entry<String, List<String>>> entrySet() {
        return this.map.entrySet();
    }
    
    @Override
    public boolean equals(final Object o) {
        return this.map.equals(o);
    }
    
    @Override
    public int hashCode() {
        return this.map.hashCode();
    }
}
