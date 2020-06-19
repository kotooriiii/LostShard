package com.github.kotooriiii.bank;

import com.github.kotooriiii.files.FileManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class BankManager {
    private final static HashMap<UUID, Bank> banks = new HashMap<>();

    public BankManager() {

    }

    public void addBank(Bank bank, boolean saveToFile) {
        banks.put(bank.getPlayerUUID(), bank);
        if (saveToFile)
            saveBank(bank);
    }

    public void saveBank(Bank bank) {
        FileManager.write(bank);
    }

    public void removeBank(Bank bank) {
        banks.remove(bank);
        FileManager.removeFile(bank);
    }


    public boolean containsBank(Bank bank) {
        return banks.containsValue(bank);
    }

    public HashMap<UUID, Bank> getBanks() {
        return banks;
    }
    public Bank wrap(UUID playerUUID) {
        return getBanks().get(playerUUID);
    }

}
