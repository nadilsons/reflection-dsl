package br.com.bit.ideias.reflection.util;

import static org.testng.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.testng.annotations.Test;

public class CollectionUtilTest {
    @Test
    public void isEmptyShoudReturnFalseIfNotNullAndHasItems() {
        String item = "abc";
        
        ArrayList<String> list = new ArrayList<String>();
        list.add(item);
        
        HashSet<String> set = new HashSet<String>();
        set.add(item);
        
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(item, item);
        
        assertFalse(CollectionUtil.isEmpty(list));
        assertFalse(CollectionUtil.isEmpty(set));
        assertFalse(CollectionUtil.isEmpty(map));
    }
    
    @Test
    public void isEmptyShoudReturnTrueIfNullOrEmpty() {
        Collection<?> nullList = null;
        Collection<?> nullSet = null;
        Map<?, ?> nullMap = null;
        Object[] nullArray = null;
        
        assertTrue(CollectionUtil.isEmpty(Collections.EMPTY_LIST));
        assertTrue(CollectionUtil.isEmpty(nullList));
        assertTrue(CollectionUtil.isEmpty(Collections.EMPTY_SET));
        assertTrue(CollectionUtil.isEmpty(nullSet));
        assertTrue(CollectionUtil.isEmpty(Collections.EMPTY_MAP));
        assertTrue(CollectionUtil.isEmpty(nullMap));
        assertTrue(CollectionUtil.isEmpty(new Date[]{}));
        assertTrue(CollectionUtil.isEmpty(nullArray));
    }
}
