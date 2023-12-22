package com.example.proj_moneymanager.Object;

import java.util.Date;

public class Plan {
    int PlanID, Tagert, Maturity;
    String PlanName, KindOfTarget, Method;
    Date Start,Finish;

    public Plan(int planID, int tagert, int maturity, String planName, String kindOfTarget, String method, Date start, Date finish) {
        PlanID = planID;
        Tagert = tagert;
        Maturity = maturity;
        PlanName = planName;
        KindOfTarget = kindOfTarget;
        Method = method;
        Start = start;
        Finish = finish;
    }

    public int getPlanID() {
        return PlanID;
    }

    public void setPlanID(int planID) {
        PlanID = planID;
    }

    public int getTagert() {
        return Tagert;
    }

    public void setTagert(int tagert) {
        Tagert = tagert;
    }

    public int getMaturity() {
        return Maturity;
    }

    public void setMaturity(int maturity) {
        Maturity = maturity;
    }

    public String getPlanName() {
        return PlanName;
    }

    public void setPlanName(String planName) {
        PlanName = planName;
    }

    public String getKindOfTarget() {
        return KindOfTarget;
    }

    public void setKindOfTarget(String kindOfTarget) {
        KindOfTarget = kindOfTarget;
    }

    public String getMethod() {
        return Method;
    }

    public void setMethod(String method) {
        Method = method;
    }

    public Date getStart() {
        return Start;
    }

    public void setStart(Date start) {
        Start = start;
    }

    public Date getFinish() {
        return Finish;
    }

    public void setFinish(Date finish) {
        Finish = finish;
    }
}
