package com.github.kotooriiii.npc.type.vendor;

import org.bukkit.inventory.ItemStack;

public class VendorItemStack {
    public ItemStack itemStack;
    public int amount;
    public double stackPrice;

    public VendorItemStack()
    {

    }

    public VendorItemStack(ItemStack itemStack, int amount, double stackPrice) {
        this.itemStack = itemStack;
        this.amount = amount;
        this.stackPrice = stackPrice;
    }

    public double getIndividualPrice()
    {
        double indivPrice = stackPrice / itemStack.getType().getMaxStackSize();
        if(indivPrice < 0.01)
        {
            return  0.01;
        }
        return indivPrice;
    }

    public double getSelectPrice(int amt)
    {
        return getIndividualPrice()*amt;
    }

    public double getRealityPriceOf(int amt) {

        if (this.amount < amt)
            return getIndividualPrice() * this.amount;
        else
            return getIndividualPrice() * amt;

    }

    public double getStackPrice()
    {
        return stackPrice;
    }

    public double getTotalPrice()
    {
        return getSelectPrice(amount);
    }

    /**
     * Sets the stack price.
     * @param itemStack The itemstack we are setting to.
     * @param amountInStack Select amount
     * @param selectPrice Select price
     */
    public void setStackPriceBy(ItemStack itemStack, int amountInStack, double selectPrice)
    {
        this.amount = amountInStack;
        this.itemStack = itemStack;

        double indivPrice = selectPrice/amountInStack;
        this.stackPrice = indivPrice*itemStack.getType().getMaxStackSize();
    }

    public int getMaxStackSize()
    {
        return itemStack.getType().getMaxStackSize();
    }

    public int getMaxAmount()
    {
        return getMaxStackSize()*VendorNPC.getMaxInnerSlots();
    }
}
