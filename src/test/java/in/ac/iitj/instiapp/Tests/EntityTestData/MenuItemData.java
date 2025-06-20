package in.ac.iitj.instiapp.Tests.EntityTestData;

import in.ac.iitj.instiapp.database.entities.Scheduling.MessMenu.MenuItem;

public enum MenuItemData {
    MENU_ITEM1("poha", "rajma", "Samosa", "chole bhature"),
    MENU_ITEM2("dosa", "paneer", "pani puri", "shamiyana"),
    MENU_ITEM3("upma","jalebi","fafda","samosa"),
    MENU_ITEM4("papad","paratha","BhelPuri","manchurian");

    public final String breakfast;
    public final String lunch;
    public final String snacks;
    public final String dinner;

    MenuItemData(String breakfast, String lunch, String snacks, String dinner) {
        this.breakfast = breakfast;
        this.lunch = lunch;
        this.snacks = snacks;
        this.dinner = dinner;
    }

    public MenuItem toEntity(){
        return  new MenuItem(this.breakfast,this.lunch,this.snacks,this.dinner);
    }
}
