package co.mcsky.townyportal.data;

import co.mcsky.townyportal.TownyPortal;
import net.md_5.bungee.api.ChatColor;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class TownModel {

    private final UUID townUUID;
    private final List<String> townBoard;
    private int shopNum;

    public TownModel(UUID townUUID) {
        this.townUUID = townUUID;
        this.townBoard = new LinkedList<>();
        this.shopNum = 0;
    }

    public TownModel(UUID townUUID, List<String> townBoard, int shopNum) {
        this.townUUID = townUUID;
        this.townBoard = townBoard;
        this.shopNum = shopNum;
    }

    public UUID getTownUUID() {
        return townUUID;
    }

    public int getTownIntroSize() {
        return townBoard.size();
    }

    public List<String> getTownBoard() {
        if (townBoard.isEmpty()) {
            return List.of(TownyPortal.text("gui.town-listing.town-entry.default-board"));
        }
        return townBoard;
    }

    public void setTownBoard(List<String> townBoard) {
        this.townBoard.clear();
        for (int i = 0; i < townBoard.size(); i++) {
            townBoard.set(i, ChatColor.GRAY + townBoard.get(i));
        }
        this.townBoard.addAll(townBoard);
    }

    public void setTownBoard(String... townBoard) {
        this.townBoard.clear();
        for (int i = 0; i < townBoard.length; i++) {
            townBoard[i] = ChatColor.GRAY + townBoard[i];
        }
        this.townBoard.addAll(Arrays.asList(townBoard));
    }

    public void setTownBoard(int line, String text) {
        this.townBoard.set(line, ChatColor.GRAY + text);
    }

    public void addTownBoard(String text) {
        townBoard.add(ChatColor.GRAY + text);
    }

    public String getTownBoard(int line) {
        return townBoard.get(line);
    }

    public int getTownBoardNum() {
        return townBoard.size();
    }

    public void deleteTownBoard(int line) {
        townBoard.remove(line);
    }

    public void clearTownBoard() {
        townBoard.clear();
    }

    public int getShopNum() {
        return shopNum;
    }

    public void setShopNum(int shopNum) {
        this.shopNum = shopNum;
    }

    public void incrementShopNum() {
        shopNum++;
    }

    public void decrementShopNum() {
        shopNum--;
        if (shopNum < 0) {
            shopNum = 0;
        }
    }

}
