package com.github.kotooriiii.bank;

import com.github.kotooriiii.files.FileManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.UUID;

public class Sale implements Comparable, Comparator {

    private UUID id;

    private UUID sellerUUID;
    private ItemStack itemStack;
    private int amount;
    private double price;

    private static ArrayList<Sale> sales = new ArrayList<>();

    public Sale(UUID id, UUID sellerUUID, ItemStack itemStack, int amount, double individualPrice) {
        this.id = id;
        this.sellerUUID = sellerUUID;
        this.itemStack = itemStack;
        this.amount = amount;
        this.price = individualPrice;
        sales.add(this);
    }

    public Sale(UUID sellerUUID, ItemStack itemStack, int amount, double individualPrice) {

        boolean uniqueExists = false;
        UUID id = null;
        existsLoop:
        while (!uniqueExists) {
            id = UUID.randomUUID();

            salesLoop:
            for (Sale sale : sales) {
                if (sale.getID().equals(id))
                    continue existsLoop;
            }
            uniqueExists = true;
        }
        this.id = id;
        this.sellerUUID = sellerUUID;
        this.itemStack = itemStack ;
        this.amount = amount;
        this.price = individualPrice;
        sales.add(this);
        FileManager.write(this);
    }

    public UUID getID() {
        return id;
    }

    public UUID getSellerUUID() {
        return sellerUUID;
    }

    public double getPrice() {
        return price;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        if (amount == 0) {
            getSales().remove(this);
            FileManager.removeFile(this);
        }
        this.amount = amount;
    }

    public static ArrayList<Sale> getSales() {
        return sales;
    }

    @Override
    public int compareTo(Object o) {
        if (o == null)
            return 1;
        if (!(o instanceof Sale))
            return 1;
        Sale otherSale = (Sale) o;

        int returnCode = 0;
        if (this.getPrice() > otherSale.getPrice()) {
            returnCode = 1;
        } else if (this.getPrice() == otherSale.getPrice()) {
            returnCode = 0;
        } else if (this.getPrice() < otherSale.getPrice()) {
            returnCode = -1;
        }

        return returnCode;
    }

    @Override
    public int compare(Object o1, Object o2) {
        if (o1 == null || o2 == null)
            return 1;
        if (!(o1 instanceof Sale) || !(o2 instanceof Sale))
            return 1;
        Sale thisSale = (Sale) o1;
        Sale otherSale = (Sale) o2;
        return thisSale.compareTo(otherSale);
    }
}
