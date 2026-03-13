// Name: Shirley Lin
// Date: 10/3/25
// Assignment: Project 4 - Inheritance & Polymorphism

import java.util.ArrayList;
import java.util.Iterator;

public class Model {
    private ArrayList<Sprite> sprites, toAdd, toRemove;
    private ArrayList<Sprite> itemsCanAdd;
    private int itemIndex;
    private Link link;

    public Model() {
        this.sprites = new ArrayList<Sprite>();
        this.toAdd = new ArrayList<Sprite>();
        this.toRemove = new ArrayList<Sprite>();
        this.itemsCanAdd = new ArrayList<Sprite>();
        this.itemIndex = 0;
        this.link = new Link();

        sprites.add(link);
        itemsCanAdd.add(new Tree(12, 12, 75, 75));
        itemsCanAdd.add(new TreasureChest(12, 12, 75, 75));
    }

    // Marshal the array list of sprites into a Json node
    public Json marshal() {
        Json ob = Json.newObject();
        Json tmpList = Json.newList();
        Json tmpList2 = Json.newList();

        for(int i = 0; i < getSpritesSize(); i++) {
            Sprite s = sprites.get(i);

            if(s.isTree())
                tmpList.add(s.marshal());
            else if(s.isTreasureChest())
                tmpList2.add(s.marshal());
        }

        ob.add("trees", tmpList);
        ob.add("treasureChests", tmpList2);
        return ob;
    }

    // Unmarshal the list of sprites from the map.json file and add them to the array list
    public void unmarshal(Json ob) {
        clearSprites();

        Json tmpList = ob.get("trees");
        for(int i = 0; i < tmpList.size(); i++) {
            Json sprite = tmpList.get(i);
            toAdd.add(new Tree(sprite));
        }

        Json tmpList2 = ob.get("treasureChests");
        for(int i = 0; i < tmpList2.size(); i++) {
            Json sprite = tmpList2.get(i);
            toAdd.add(new TreasureChest(sprite));
        }
    }

    public void update() {
        Iterator<Sprite> it1 = sprites.iterator();

        while(it1.hasNext()) {
            Sprite sprite1 = it1.next();
            
            // Update sprite and check if it's still valid
            if(sprite1.update()) {
                Iterator<Sprite> it2 = sprites.iterator();

                while(it2.hasNext()) {
                    Sprite sprite2 = it2.next();

                    // Check to make sure the two sprites are NOT the same and whether or not they are colliding
                    if((sprite1 != sprite2) && checkCollision(sprite1, sprite2)) {
                        // Link and Tree are colliding
                        if(sprite1.isLink() && sprite2.isTree())
                            link.fixCollision(sprite2);
                        // Link and TreasureChest are colliding
                        else if(sprite1.isLink() && sprite2.isTreasureChest()) {
                            link.fixCollision(sprite2);

                            // Delete the the treasure chest if it's open and the buffer time is up; otherwise open it
                            if(((TreasureChest)sprite2).getCanCollect())
                                ((TreasureChest)sprite2).deleteChest();
                            else
                                ((TreasureChest)sprite2).openChest();
                        }
                        // Boomerang and TreasureChest are colliding
                        else if (sprite1.isBoomerang() && sprite2.isTreasureChest()) {
                            ((Boomerang)sprite1).deleteBoomerang();

                            // Same interaction as with Link, but it's a Boomerang
                            if(((TreasureChest)sprite2).getCanCollect())
                                ((TreasureChest)sprite2).deleteChest();
                            else
                                ((TreasureChest)sprite2).openChest();
                        }
                        // Boomerang and Tree are colliding
                        else if(sprite1.isBoomerang() && sprite2.isTree())
                            ((Boomerang)sprite1).deleteBoomerang();
                    }
                }
            } else
                // If the sprite is invalid, remove it
                it1.remove();
        }

        // Prevents concurrent modification exception by only modifying the sprites array in Model
        sprites.addAll(toAdd);
        toAdd.clear();
        sprites.removeAll(toRemove);
        toAdd.clear();
    }

    // Check if two sprites are colliding
    public boolean checkCollision(Sprite a, Sprite b) {
        int aLeft = a.getX();
        int aRight = a.getX() + a.getW();
        int aTop = a.getY();
        int aBottom = a.getY() + a.getH();

        int bLeft = b.getX();
        int bRight = b.getX() + b.getW();
        int bTop = b.getY();
        int bBottom = b.getY() + b.getH();

        // Checks when the sprites are not colliding
        if(aRight < bLeft)
            return false;
        if(aLeft > bRight)
            return false;
        if(aBottom < bTop)
            return false;
        if(aTop > bBottom)
            return false;

        return true;
    }

    // Called by Controller when the user is adding sprites to the map to ensure that no two sprites overlap
    public boolean imageCollision(Sprite a) {
        int aLeft = a.getX();
        int aRight = a.getX() + a.getW();
        int aTop = a.getY();
        int aBottom = a.getY() + a.getH();

        // Loop through every sprite in the array list and check for overlap
        for(int i = 0; i < sprites.size(); i++) {
            Sprite b = sprites.get(i);
            
            int bLeft = b.getX();
            int bRight = b.getX() + b.getW();
            int bTop = b.getY();
            int bBottom = b.getY() + b.getH();

            // If any of these conditions are true, then the sprites do not overlap, so continue to the next sprite
            if(aRight <= bLeft)
                continue;
            if(aLeft >= bRight)
                continue;
            if(aBottom <= bTop)
                continue;
            if(aTop >= bBottom)
                continue;
            
            // If all of the previous checks fail, then the sprites overlap
            return true;
        }
        
        return false;
    }

    // Called by Controller to create a Boomerang when the user presses space
    public void createBoomerang() {
        int x = link.getX() + link.getW() / 2;
        int y = link.getY() + link.getH() / 2;

        toAdd.add(new Boomerang(x, y, link.getDirection()));
    }

    // Called by Controller to let Model know that an arrow key has been pressed
    public void moveLink(String direction) {
        link.updateLocation(direction); // Moves Link in the corresponding direction
        link.setUpdateImage(true); // Tells Link to start animating
    }

    // Called by Controller to let Model know that no arrow key is being pressed
    public void resetLink() {
        link.setUpdateImage(false); // Tells Link to stop animating
    }

    public void savePreviousPosition() {
        link.setPX();
        link.setPY();
    }

    // Called by View to check if Link has crossed any of the room boundaries
    public int crossBoundary(int currentRoomX, int currentRoomY) {
        int direction = 0; // 1: right, 2: left, 3: up, 4: down
        int linkX = link.getX() + Link.LINK_WIDTH; // Adjust for the size of Link's model
        int linkY = link.getY() + Link.LINK_HEIGHT;

        if(linkX - currentRoomX > Game.WINDOW_WIDTH) // Check right boundary
            direction = 1;
        else if(linkX < currentRoomX) // Check left boundary
            direction = 2;
        else if(linkY < currentRoomY) // Check upper boundary
            direction = 3;
        else if(linkY - currentRoomY > Game.WINDOW_HEIGHT) // Check lower boundary
            direction = 4;

        return direction;
    }

    public void addSprite(Sprite s) {
        toAdd.add(s);
    }

    public void clearSprites() {
        sprites.clear();
        sprites.add(link);
    }

    public void removeSprite(int mouseX, int mouseY, int currentRoomX, int currentRoomY) {
        Sprite item = getItemCanAdd();

        // Loop through every item in the array list sprites and check if the user clicked on it
        for(int i = 0; i < sprites.size(); i++) {
            Sprite s = sprites.get(i);
            if(s.spriteExists(mouseX + currentRoomX, mouseY + currentRoomY)) {
                // If there is a sprite where the user clicked, check if it's the same item being removed
                if((s.isTree() && item.isTree()) || ( s.isTreasureChest() && item.isTreasureChest()))
                    toRemove.add(s);
            }
        }
    }

    public int getSpritesSize() {
        return sprites.size();
    }

    public Sprite getSprite(int index) {
        return sprites.get(index);
    }

    public Sprite getItemCanAdd() {
        return itemsCanAdd.get(itemIndex);
    }

    public int getItemIndex() {
        return itemIndex;
    }

    public void setItemIndex() {
        if(itemIndex >= itemsCanAdd.size() - 1)
            itemIndex = 0;
        else
            itemIndex++;
    }
}