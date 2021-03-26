package com.github.kotooriiii.npc.type.vendor;

import com.github.kotooriiii.structure.FixedLinkedList;
import net.citizensnpcs.api.persistence.Persister;
import net.citizensnpcs.api.util.DataKey;

public class FixedLinkedListStringPersister implements Persister<FixedLinkedList<String>> {

    private static final String KEY = "list";
    private static final String KEY_ERROR = "NULL-DEFAULT";

    @Override
    public FixedLinkedList<String> create(DataKey dataKey) {

        final FixedLinkedList<String> strings = new FixedLinkedList<String>(VendorNPC.getMaxHistory());

        for(int i = 0; i < VendorNPC.getMaxHistory(); i++)
        {
            final String receivedKey = dataKey.getString(KEY + "." + i, KEY_ERROR);

            if(receivedKey.equals(KEY_ERROR))
                break;
            strings.add(receivedKey);
        }

        dataKey.removeKey(KEY);

        return strings;
    }

    @Override
    public void save(FixedLinkedList<String> real, DataKey root) {
        for(int i = 0; i < real.size(); i++)
        {
            root.setString(KEY + "." + i, real.get(i));
        }
    }
}
