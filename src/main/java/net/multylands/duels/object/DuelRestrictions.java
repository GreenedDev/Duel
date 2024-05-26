package net.multylands.duels.object;

public class DuelRestrictions {
    boolean isComplete;

    boolean isBowAllowed;
    boolean isNotchAllowed;
    boolean isPotionAllowed;
    boolean isGoldenAppleAllowed;
    boolean isShieldAllowed;
    boolean isTotemAllowed;
    boolean isElytraAllowed;
    boolean isEnderPearlAllowed;

    boolean isKeepInventoryEnabled;
    boolean isInventorySavingEnabled;


    public DuelRestrictions(boolean bowAllowed, boolean notchAllowed,
                            boolean potionsAllowed, boolean goldenAppleAllowed,
                            boolean shieldsAllowed, boolean totemsAllowed, boolean isElytraAllowed,
                            boolean isEnderPearlAllowed, boolean isComplete, boolean isKeepInventoryEnabled,
                            boolean isInventorySavingEnabled) {
        this.isComplete = isComplete;

        this.isBowAllowed = bowAllowed;
        this.isNotchAllowed = notchAllowed;
        this.isGoldenAppleAllowed = goldenAppleAllowed;
        this.isPotionAllowed = potionsAllowed;
        this.isShieldAllowed = shieldsAllowed;
        this.isTotemAllowed = totemsAllowed;
        this.isElytraAllowed = isElytraAllowed;
        this.isEnderPearlAllowed = isEnderPearlAllowed;

        this.isKeepInventoryEnabled = isKeepInventoryEnabled;
        this.isInventorySavingEnabled = isInventorySavingEnabled;
    }

    public boolean isBowAllowed() {
        return isBowAllowed;
    }

    public boolean isElytraAllowed() {
        return isElytraAllowed;
    }

    public boolean isEnderPearlAllowed() {
        return isEnderPearlAllowed;
    }

    public boolean isKeepInventoryEnabled() {
        return isKeepInventoryEnabled;
    }

    public boolean isNotchAllowed() {
        return isNotchAllowed;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public boolean isPotionAllowed() {
        return isPotionAllowed;
    }

    public boolean isGoldenAppleAllowed() {
        return isGoldenAppleAllowed;
    }

    public boolean isShieldAllowed() {
        return isShieldAllowed;
    }

    public boolean isTotemAllowed() {
        return isTotemAllowed;
    }
    public boolean isInventorySavingEnabled() {
        return isInventorySavingEnabled;
    }

    public void setInventorySaving(boolean inventorySavingEnabled) {
        isInventorySavingEnabled = inventorySavingEnabled;
    }
    public void setComplete(boolean yesOrNot) {
        isComplete = yesOrNot;
    }
    public void setBow(boolean yesOrNot) {
        isBowAllowed = yesOrNot;
    }

    public void setNotch(boolean yesOrNot) {
        isNotchAllowed = yesOrNot;
    }

    public void setPotionAllowed(boolean yesOrNot) {
        isPotionAllowed = yesOrNot;
    }

    public void setGoldenApple(boolean yesOrNot) {
        isGoldenAppleAllowed = yesOrNot;
    }

    public void setShield(boolean yesOrNot) {
        isShieldAllowed = yesOrNot;
    }

    public void setTotem(boolean yesOrNot) {
        isTotemAllowed = yesOrNot;
    }

    public void setElytra(boolean yesOrNot) {
        isElytraAllowed = yesOrNot;
    }

    public void setKeepInventory(boolean yesOrNot) {
        isKeepInventoryEnabled = yesOrNot;
    }

    public void setEnderPearl(boolean yesOrNot) {
        isEnderPearlAllowed = yesOrNot;
    }

    public String getEnabled() {
        StringBuilder builder = new StringBuilder();
        if (isNotchAllowed) {
            builder.append("Notch,");
        }
        if (isPotionAllowed) {
            builder.append("Potions,");
        }
        if (isGoldenAppleAllowed) {
            builder.append("Golden apple,");
        }
        if (isShieldAllowed) {
            builder.append("Shields,");
        }
        if (isTotemAllowed) {
            builder.append("Totems,");
        }
        if (isBowAllowed) {
            builder.append("Bow,");
        }
        if (isElytraAllowed) {
            builder.append("Elytra,");
        }
        if (isEnderPearlAllowed) {
            builder.append("Ender Pearl,");
        }
        String finalString = builder.toString();
        if (finalString.isEmpty()) {
            return null;
        }
        String end = finalString.replace(finalString.substring(0, finalString.length() - 1), "");
        if (end.equals(",")) {
            return finalString.substring(0, finalString.length() - 1) + ".";
        } else {
            return null;
        }
    }

    public String getDisabled() {
        StringBuilder builder = new StringBuilder();
        if (!isNotchAllowed) {
            builder.append("Notch,");
        }
        if (!isPotionAllowed) {
            builder.append("Potions,");
        }
        if (!isGoldenAppleAllowed) {
            builder.append("Golden apple,");
        }
        if (!isShieldAllowed) {
            builder.append("Shields,");
        }
        if (!isTotemAllowed) {
            builder.append("Totems,");
        }
        if (!isBowAllowed) {
            builder.append("Bow,");
        }
        if (!isElytraAllowed) {
            builder.append("Elytra,");
        }
        if (!isEnderPearlAllowed) {
            builder.append("Ender Pearl,");
        }
        String finalString = builder.toString();
        if (finalString.isEmpty()) {
            return null;
        }
        String end = finalString.replace(finalString.substring(0, finalString.length() - 1), "");
        if (end.equals(",")) {
            return finalString.substring(0, finalString.length() - 1) + ".";
        } else {
            return null;
        }
    }
}
