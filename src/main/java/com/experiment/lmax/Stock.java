package com.experiment.lmax;

public class Stock {
    private int stockId;
    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    private String stockName;

    public Stock(int stockId){
        this.stockId = stockId;
    }

    public Stock(){

    }

    public int getStockId() {
        return stockId;
    }

    public void setStockId(int stockId) {
        this.stockId = stockId;
    }
}
