package com.cy.yigym.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by lijianqiang on 15/11/30.
 */
@DatabaseTable(tableName = "tb_growth_record")
public class GrowthRecordBean {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(columnName = "medalNumber")
    private int medalNumber;

    @DatabaseField(columnName = "signContent")
    private String signContent;

    public int getId() {
        return id;
    }


    public String getSignContent() {
        return signContent;
    }

    public void setSignContent(String signContent) {
        this.signContent = signContent;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMedalNumber() {
        return medalNumber;
    }

    public void setMedalNumber(int medalNumber) {
        this.medalNumber = medalNumber;
    }
}
