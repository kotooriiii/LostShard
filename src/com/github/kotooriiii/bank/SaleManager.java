package com.github.kotooriiii.bank;

import com.github.kotooriiii.files.FileManager;

import java.util.ArrayList;

public class SaleManager {
    private  ArrayList<Sale> sales = new ArrayList<>();

    public SaleManager() {

    }

    public void addSale(Sale sale, boolean saveToFile) {
        sales.add(sale);
        if (saveToFile)
            saveSale(sale);
    }

    public void saveSale(Sale sale) {
        FileManager.write(sale);
    }

    public void removeSale(Sale sale) {
        sales.remove(sale);
        FileManager.removeFile(sale);
    }


    public boolean containsSale(Sale sale) {
        return sales.contains(sale);
    }


    public  ArrayList<Sale> getSales() {
        return sales;
    }


}
