package model;

// Holds one aggregated row of the item-wise sales report
public class ItemSalesSummary {
    
    private int medicineId;
    private String medicineName;
    private int totalQuantitySold;
    private double totalRevenue;

    // Constructors
    public ItemSalesSummary() { }

    public ItemSalesSummary(int medicineId, String medicineName, int totalQuantitySold, double totalRevenue) {
        
        this.medicineId = medicineId;
        this.medicineName = medicineName;
        this.totalQuantitySold = totalQuantitySold;
        this.totalRevenue = totalRevenue;
    
    }

    // Getters and Setters
    public int getMedicineId() { return medicineId; }

    public void setMedicineId(int medicineId) { this.medicineId = medicineId; }

    public String getMedicineName() { return medicineName; }

    public void setMedicineName(String medicineName) { this.medicineName = medicineName; }

    public int getTotalQuantitySold() { return totalQuantitySold; }

    public void setTotalQuantitySold(int totalQuantitySold) { this.totalQuantitySold = totalQuantitySold; }

    public double getTotalRevenue() { return totalRevenue; }

    public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }

    // Returns object as str
    @Override
    public String toString() { return "ItemSalesSummary{ medicineName='" + medicineName + "', totalQuantitySold=" + totalQuantitySold + ", totalRevenue=" + totalRevenue + " }"; }
    
}
