// 
// Decompiled by Procyon v0.5.36
// 

package ir.am3n.needtool.webserver.sun;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.Map;
import java.util.List;
import ir.am3n.needtool.webserver.Headers;

class UnmodifiableHeaders extends Headers
{
    Headers map;
    
    UnmodifiableHeaders(final Headers map) {
        this.map = map;
    }
    
    @Override
    public int size() {
        return this.map.size();
    }
    
    @Override
    public boolean isEmpty() {
        return this.map.isEmpty();
    }
    
    @Override
    public boolean containsKey(final Object o) {
        return this.map.containsKey(o);
    }
    
    @Override
    public boolean containsValue(final Object o) {
        return this.map.containsValue(o);
    }
    
    @Override
    public List<String> get(final Object o) {
        return this.map.get(o);
    }
    
    @Override
    public String getFirst(final String s) {
        return this.map.getFirst(s);
    }
    
    @Override
    public List<String> put(final String s, final List<String> list) {
        return this.map.put(s, list);
    }
    
    @Override
    public void add(final String s, final String s2) {
        throw new UnsupportedOperationException("unsupported operation");
    }
    
    @Override
    public void set(final String s, final String s2) {
        throw new UnsupportedOperationException("unsupported operation");
    }
    
    @Override
    public List<String> remove(final Object o) {
        throw new UnsupportedOperationException("unsupported operation");
    }
    
    @Override
    public void putAll(final Map<? extends String, ? extends List<String>> map) {
        throw new UnsupportedOperationException("unsupported operation");
    }
    
    @Override
    public void clear() {
        throw new UnsupportedOperationException("unsupported operation");
    }
    
    @Override
    public Set<String> keySet() {
        return Collections.unmodifiableSet((Set<? extends String>)this.map.keySet());
    }
    
    @Override
    public Collection<List<String>> values() {
        return Collections.unmodifiableCollection((Collection<? extends List<String>>)this.map.values());
    }
    
    @Override
    public Set<Map.Entry<String, List<String>>> entrySet() {
        return Collections.unmodifiableSet((Set<? extends Map.Entry<String, List<String>>>)this.map.entrySet());
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
