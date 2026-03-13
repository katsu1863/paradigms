# Name: Shirley Lin
# Date: 11/26/25
# Assignment: Project 7 - Link in Python

import pygame
import time
import json
import math

from pygame.locals import*
from time import sleep

class Sprite():
    def __init__(self, x, y, w, h):
        self.x = x
        self.y = y
        self.w = w
        self.h = h
        self.valid = True

    def update(self):
        return self.valid

    def is_link(self):
        return False
    
    def is_tree(self):
        return False
    
    def is_treasure_chest(self):
        return False
    
    def is_boomerang(self):
        return False
    
    def is_cucco(self):
        return False

    def marshal(self):
        return {
            "x": self.x,
            "y": self.y,
        }
    
class Link(Sprite):

    WIDTH = 45
    HEIGHT = 45
    STARTING_X = 80
    STARTING_Y = 80

    rupees_collected = 0

    def reset_rupee():
        Link.rupees_collected = 0

    def __init__(self):
        super().__init__(Link.STARTING_X, Link.STARTING_Y, Link.WIDTH, Link.HEIGHT)

        self.px = self.x
        self.py = self.y
        self.image_index = 0
        self.direction = 0 # 0 - down, 1 - left, 2 - right, 3 - up
        self.animate = False
        self.speed = 8

        self.TOTAL_NUM_IMAGES = 44
        self.NUM_DIRECTIONS = 4
        self.MAX_IMAGES_PER_DIRECTION = 11
        self.images = []

        # Load all the Link images
        index = 1
        for i in range(self.NUM_DIRECTIONS):
            self.images.append([])
            for j in range(self.MAX_IMAGES_PER_DIRECTION):
                self.images[i].append(pygame.image.load("images/link" + str(index) + ".png"))
                index += 1

    def update(self):
        # Animate Link
        # Reset his sprite to a standing position if no arrow keys are being pressed
        if self.animate and self.image_index < self.MAX_IMAGES_PER_DIRECTION - 1:
            self.image_index += 1
        else:
            self.image_index = 0

        return self.valid

    def draw_yourself(self, g, shift_x, shift_y):
        LOCATION = (self.x - shift_x, self.y - shift_y)
        SIZE = (self.w, self.h)
        g.blit(pygame.transform.scale(self.images[self.direction][self.image_index], SIZE), LOCATION)

    def update_location(self, direction):
        self.animate = True

        match direction:
            case "left":
                self.direction = 1
                self.x -= self.speed
            case "right":
                self.direction = 2
                self.x += self.speed
            case "up":
                self.direction = 3
                self.y -= self.speed
            case "down":
                self.direction = 0
                self.y += self.speed

    def fix_collision(self, sprite):
        # Link collides from left
        if (self.px + self.w <= sprite.x) and (self.x + self.w >= sprite.x):
            self.x = sprite.x - self.w - 1
        # Link collides from right
        elif (self.px >= sprite.x + sprite.w) and (self.x <= sprite.x + sprite.w):
            self.x = sprite.x + sprite.w + 1
        # Link collides from top
        if (self.py + self.h <= sprite.y) and (self.y + self.h >= sprite.y):
            self.y = sprite.y - self.h - 1
        # Link collides from bottom
        elif (self.py >= sprite.y + sprite.h) and (self.y <= sprite.y + sprite.h):
            self.y = sprite.y + sprite.h + 1

    def is_link(self):
        return True
    
class Cucco(Sprite):

    WIDTH = 30
    HEIGHT = 30

    num_of_cuccos = 0
    num_of_hits = 0
    num_disappeared = 0
    is_angry = False

    link_x = Link.STARTING_X
    link_y = Link.STARTING_Y

    def __init__(self, x, y, w = None, h = None):
        if w is None or h is None:
            super().__init__(x, y, Cucco.WIDTH, Cucco.HEIGHT)
            Cucco.num_of_cuccos += 1
        else:
            super().__init__(x, y, w, h)

        self.px = self.x
        self.py = self.y
        self.x_dir = 0 # 0 - left, 1 - right
        self.y_dir = 0 # 0 - down, 1 - up
        self.image_index = 0
        self.attached_to_link = False
        self.attached_timer = 20
        self.speed = 4
        self.angry_speed = 10

        self.TOTAL_NUM_IMAGES = 4
        self.NUM_DIRECTIONS = 2
        self.MAX_IMAGES_PER_DIRECTION = 2

        # Load all the Cucco images into two separate lists
        # One for regular Cuccos and another for angry Cuccos
        self.images = []
        index = 1
        for i in range(self.NUM_DIRECTIONS):
            self.images.append([])
            for j in range(self.MAX_IMAGES_PER_DIRECTION):
                self.images[i].append(pygame.image.load("images/cucco" + str(index) + ".png"))
                index += 1
        
        self.angry_images = []
        index = 1
        for i in range(self.NUM_DIRECTIONS):
            self.angry_images.append([])
            for j in range(self.MAX_IMAGES_PER_DIRECTION):
                self.angry_images[i].append(pygame.image.load("images/angrycucco" + str(index) + ".png"))
                index += 1

    def update(self):
        # Save Cucco's previous position for collision fixing
        self.px = self.x
        self.py = self.y

        # Animate the Cucco
        if self.image_index >= self.MAX_IMAGES_PER_DIRECTION - 1:
            self.image_index = 0
        else:
            self.image_index += 1

        # Once 3 Cuccos have disappeared or there's only 1 cucco, make them calm down & reset values
        if Cucco.num_disappeared >= 3 or Cucco.num_of_cuccos <= 1:
            self.attached_to_link = False
            self.attached_timer = 20
            Cucco.num_disappeared = 0
            Cucco.num_of_hits = 0
            Cucco.is_angry = False

        # Cuccos will bounce around the map if they aren't angry
        if not(Cucco.is_angry):
            if self.x_dir == 0:
                self.x -= self.speed
            elif self.x_dir == 1:
                self.x += self.speed
            if self.y_dir == 0:
                self.y += self.speed
            elif self.y_dir == 1:
                self.y -= self.speed
        # Otherwise they'll flock to Link's location
        else:
            # Calculate Link's location
            dx = Cucco.link_x - self.x
            dy = Cucco.link_y - self.y
            length = math.sqrt(dx * dx + dy * dy)

            if length < 0.001:
                length = 0.001
            dir_to_go_x = dx / length
            dir_to_go_y = dy / length

            self.x += dir_to_go_x * self.angry_speed
            self.y += dir_to_go_y * self.angry_speed

        # If they're attached to Link, tick down a timer
        # After 20 frames, the Cucco will disappear
        if self.attached_to_link:
            if self.attached_timer > 0:
                self.attached_timer -= 1
            else:
                self.valid = False
                Cucco.num_of_cuccos -= 1
                Cucco.num_disappeared += 1
            
        return self.valid

    def draw_yourself(self, g, shift_x, shift_y):
        LOCATION = (self.x - shift_x, self.y - shift_y)
        SIZE = (self.w, self.h)
        if Cucco.is_angry:
            g.blit(pygame.transform.scale(self.angry_images[self.x_dir][self.image_index], SIZE), LOCATION)
        else:
            g.blit(pygame.transform.scale(self.images[self.x_dir][self.image_index], SIZE), LOCATION)

    def fix_collision(self, sprite):
        # Change the direction of the Cucco once they collide with another sprite
        if (self.px + self.w <= sprite.x) and (self.x + self.w >= sprite.x):
            self.x = sprite.x - self.w - 1
            self.x_dir = 0
        elif (self.px >= sprite.x + sprite.w) and (self.x <= sprite.x + sprite.w):
            self.x = sprite.x + sprite.w + 1
            self.x_dir = 1
        if (self.py + self.h <= sprite.y) and (self.y + self.h >= sprite.y):
            self.y = sprite.y - self.h - 1
            self.y_dir = 1
        elif (self.py >= sprite.y + sprite.h) and (self.y <= sprite.y + sprite.h):
            self.y = sprite.y + sprite.h + 1
            self.y_dir = 0

    def is_cucco(self):
        return True
    
class Tree(Sprite):

    WIDTH = 75
    HEIGHT = 75

    def __init__(self, x, y, w = None, h = None):
        if w is None or h is None:
            super().__init__(x, y, Tree.WIDTH, Tree.HEIGHT)
        else:
            super().__init__(x, y, w, h)

        self.image = pygame.image.load("images/tree.png")

    def draw_yourself(self, g, shift_x, shift_y):
        LOCATION = (self.x - shift_x, self.y - shift_y)
        SIZE = (self.w, self.h)
        g.blit(pygame.transform.scale(self.image, SIZE), LOCATION)

    def is_tree(self):
        return True
    
class TreasureChest(Sprite):

    WIDTH = 35
    HEIGHT = 35

    def __init__(self, x, y, w = None, h = None):
        if w is None or h is None:
            super().__init__(x, y, TreasureChest.WIDTH, TreasureChest.HEIGHT)
        else:
            super().__init__(x, y, w, h)

        self.image_index = 0
        self.can_collect = False
        self.countdown = 45

        self.images = []
        self.images.append(pygame.image.load("images/treasurechest.png"))
        self.images.append(pygame.image.load("images/rupee.png"))

    def update(self):
        # Start the countdown once Link or Boomerang collides with the chest
        if self.image_index == 1:
            self.countdown -= 1
            # Delete the rupee after 45 frames
            if self.countdown <= 0:
                self.valid = False
            # Link can collect the rupee after a buffer time of 5 frames
            elif self.countdown < 40:
                self.can_collect = True

        return self.valid
    
    def draw_yourself(self, g, shift_x, shift_y):
        LOCATION = (self.x - shift_x, self.y - shift_y)
        SIZE = (self.w, self.h)
        g.blit(pygame.transform.scale(self.images[self.image_index], SIZE), LOCATION)

    def open_chest(self):
        if self.image_index == 0:
            self.image_index = 1

    def is_treasure_chest(self):
        return True
    
class Boomerang(Sprite):

    WIDTH = 20
    HEIGHT = 20

    def __init__(self, x, y, direction):
        super().__init__(x, y, Boomerang.WIDTH, Boomerang.HEIGHT)

        self.speed = 12
        self.direction = direction
        self.image_index = 0

        # Load all the Boomerang images
        self.TOTAL_NUM_IMAGES = 4
        self.images = []
        for i in range(self.TOTAL_NUM_IMAGES):
            self.images.append(pygame.image.load("images/boomerang" + str(i + 1) + ".png"))
    
    def update(self):
        # Animate Boomerang
        if self.image_index >= self.TOTAL_NUM_IMAGES - 1:
            self.image_index = 0
        else:
            self.image_index += 1

        # Moves the Boomerang across the screen in the direction Link is facing
        match self.direction:
            case 0:
                self.y += self.speed
            case 1:
                self.x -= self.speed
            case 2:
                self.x += self.speed
            case 3:
                self.y -= self.speed
        
        return self.valid
    
    def draw_yourself(self, g, shift_x, shift_y):
        LOCATION = (self.x - shift_x, self.y - shift_y)
        SIZE = (self.w, self.h)
        g.blit(pygame.transform.scale(self.images[self.image_index], SIZE), LOCATION)

    def is_boomerang(self):
        return True

class Model():
    filename = "map.json"
    
    def __init__(self):
        self.sprites = []
        self.to_add = []
        self.to_remove = []
        self.items_can_add = []
        self.item_index = 0

        self.link = Link()
        self.sprites.append(self.link)

        self.items_can_add.append(Tree(12, 12, 75, 75))
        self.items_can_add.append(TreasureChest(12, 12, 75, 75))
        self.items_can_add.append(Cucco(12, 12, 75, 75))

        self.load_map()

    def load_map(self):
        # Clear sprites, add Link back, and reset rupee count
        self.clear_map()

        # Open Json file and extract the lists of sprite objects
        with open(Model.filename) as file:
            data = json.load(file)
            trees = data["trees"]
            treasure_chests = data["treasure_chests"]
            cuccos = data["cuccos"]
        file.close()

        # For each lists, pull the coordinates and create an instance of the corresponding sprite
        for entry in trees:
            self.sprites.append(Tree(entry["x"], entry["y"]))
        for entry in treasure_chests:
            self.sprites.append(TreasureChest(entry["x"], entry["y"]))
        for entry in cuccos:
            self.sprites.append(Cucco(entry["x"], entry["y"]))

    def save_map(self):
        trees = []
        treasure_chests = []
        cuccos = []

        # Marshal all the sprites into the appropriate lists
        for sprite in self.sprites:
            if sprite.is_tree():
                trees.append(sprite.marshal())
            elif sprite.is_treasure_chest():
                treasure_chests.append(sprite.marshal())
            elif sprite.is_cucco():
                cuccos.append(sprite.marshal())

        map_to_save = {
            "trees": trees,
            "treasure_chests": treasure_chests,
            "cuccos": cuccos
        }

        # Save to file
        with open(Model.filename, "w") as f:
            json.dump(map_to_save, f)

    def update(self):
        # Loop through all the sprites, update them, and check for collision
        for sprite1 in self.sprites:
            # Remove the sprite if it's no longer valid
            if not(sprite1.update()):
                self.to_remove.append(sprite1)
                continue

            # Handle collisions between sprites
            for sprite2 in self.sprites:
                # Don't check for collision if the sprites are the same
                if sprite1 is sprite2:
                    continue
                if self.check_collision(sprite1, sprite2):
                    # Handle collisions with Link
                    if sprite1.is_link():
                        self.link.fix_collision(sprite2)
                        if sprite2.is_treasure_chest():
                            if sprite2.can_collect:
                                sprite2.valid = False
                                Link.rupees_collected += 1
                            else:
                                sprite2.open_chest()
                        elif sprite2.is_cucco():
                            Cucco.num_of_hits += 1
                            if Cucco.is_angry:
                                sprite2.attached_to_link = True
                    # Handle collisions with Boomerang
                    elif sprite1.is_boomerang():
                        if not sprite2.is_link():
                            sprite1.valid = False
                        
                        if sprite2.is_treasure_chest():
                            if sprite2.can_collect:
                                sprite2.valid = False
                                Link.rupees_collected += 1
                            else:
                                sprite2.open_chest()
                        elif sprite2.is_cucco():
                            Cucco.num_of_hits += 1
                    # Handle collisions with Cucco
                    elif sprite1.is_cucco():
                        if not Cucco.is_angry:
                            sprite1.fix_collision(sprite2)
                        
                        if sprite2.is_link():
                            Cucco.num_of_hits += 1
                    
                if Cucco.num_of_hits >= 5:
                    Cucco.is_angry = True

        # Add and remove any necessary sprites after all the collision checking is done
        self.sprites.extend(self.to_add)
        self.to_add.clear()

        for item in self.to_remove:
            self.sprites.remove(item)
        self.to_remove.clear()

    def check_collision(self, spriteA, spriteB):
        if spriteA.x > spriteB.x + spriteB.w:
            return False
        if spriteA.x + spriteA.w < spriteB.x:
            return False
        if spriteA.y > spriteB.y + spriteB.h:
            return False
        if spriteA.y + spriteA.h < spriteB.y:
            return False
        
        return True
    
    def image_collides(self, sprite1):
        # Loop through sprites and check for overlap
        for sprite2 in self.sprites:
            # If any of these conditions are true, the sprites do not overlap
            if (sprite1.x + sprite1.w) <= sprite2.x:
                continue
            if sprite1.x >= (sprite2.x + sprite2.w):
                continue
            if (sprite1.y + sprite1.h) <= sprite2.y:
                continue
            if sprite1.y >= (sprite2.y + sprite2.h):
                continue

            return True
        
        return False
    
    def cross_boundary(self, shift_x, shift_y):
        linkX = self.link.x + self.link.w
        linkY = self.link.y + self.link.h

        if linkX - shift_x > View.SCREEN_WIDTH:
            return 1
        elif linkX < shift_x:
            return 2
        elif linkY < shift_y:
            return 3
        elif linkY - shift_y > View.SCREEN_HEIGHT:
            return 4
        
        return -1
    
    def create_boomerang(self):
        x = self.link.x + self.link.w / 2
        y = self.link.y + self.link.h / 2

        self.to_add.append(Boomerang(x, y, self.link.direction))

    def update_item_index(self):
        if self.item_index >= len(self.items_can_add) - 1:
            self.item_index = 0
        else:
            self.item_index += 1

    def clear_map(self):
        self.sprites.clear()
        self.sprites.append(self.link)
        Link.reset_rupee()

class View():

    SCREEN_WIDTH = 800
    SCREEN_HEIGHT = 600
    EDIT_BOX_WIDTH = 100
    EDIT_BOX_HEIGHT = 100

    def __init__(self, model):
        SCREEN_SIZE = (View.SCREEN_WIDTH, View.SCREEN_HEIGHT)
        self.screen = pygame.display.set_mode(SCREEN_SIZE, 32)
        self.model = model
        self.shift_x = 0
        self.shift_y = 0

    def update(self):
        GREEN_COLOR = (72, 152, 72)
        LIGHT_GREEN_COLOR = (146, 203, 146)

        # Change background color if the user is in edit_mode
        if Controller.edit_mode:
            self.screen.fill(LIGHT_GREEN_COLOR)
        else:
            self.screen.fill(GREEN_COLOR)

        # Draw sprites to the screen
        for sprite in self.model.sprites:
            sprite.draw_yourself(self.screen, self.shift_x, self.shift_y)

        # Display the number of rupees Link has collected
        font = pygame.font.SysFont(None, 32)   
        text_string = "Link has collected " + str(Link.rupees_collected) + " rupees!"
        WHITE_COLOR = (255, 255, 255)
        text_surface = font.render(text_string, True, WHITE_COLOR)
        TEXT_LOCATION = (250, 10)
        self.screen.blit(text_surface, TEXT_LOCATION)

        # Display the map item being added if user is in edit_mode
        if Controller.edit_mode:
            pygame.draw.rect(self.screen, GREEN_COLOR, (0, 0, View.EDIT_BOX_WIDTH, View.EDIT_BOX_HEIGHT))
            sprite = self.model.items_can_add[self.model.item_index]
            sprite.draw_yourself(self.screen, 0, 0)

        # Updates camera view as Link crossess the room boundaries
        direction = self.model.cross_boundary(self.shift_x, self.shift_y)
        match direction:
            case 1:
                self.shift_x += View.SCREEN_WIDTH
            case 2:
                self.shift_x -= View.SCREEN_WIDTH
            case 3:
                self.shift_y -= View.SCREEN_HEIGHT
            case 4:
                self.shift_y += View.SCREEN_HEIGHT
        
        # Update display screen
        pygame.display.flip()

class Controller():
    edit_mode = False
    
    def __init__(self, model, view):
        self.model = model
        self.view = view
        self.keep_going = True
        self.key_space = False

    def update(self):
        # Save Link's previous position for collision fixing
        self.model.link.px = self.model.link.x
        self.model.link.py = self.model.link.y

        # Update Link's location according to the user's key presses
        keys = pygame.key.get_pressed()
        if keys[K_LEFT]:
            self.model.link.update_location("left")
        if keys[K_RIGHT]:
            self.model.link.update_location("right")
        if keys[K_UP]:
            self.model.link.update_location("up")
        if keys[K_DOWN]:
            self.model.link.update_location("down")

        # Stop animating Link once no arrow keys are being pressed
        if not(keys[K_LEFT] or keys[K_RIGHT] or keys[K_UP] or keys[K_DOWN]):
            self.model.link.animate = False

        for event in pygame.event.get():
            if event.type == QUIT:
                self.keep_going = False
            elif event.type == KEYDOWN:
                # esc/q - Quit the game
                if event.key == K_ESCAPE or event.key == K_q:
                    self.keep_going = False
                # Space - Create a Boomerang from Link's position
                elif event.key == K_SPACE:
                    if not(self.key_space):
                        self.model.create_boomerang()
                        self.key_space = True 
            elif event.type == pygame.MOUSEBUTTONUP:
                mouse_pos = pygame.mouse.get_pos()
                mouse_x = mouse_pos[0]
                mouse_y = mouse_pos[1]

                # Handle mouse clicks while the user is in edit_mode
                if Controller.edit_mode:
                    # If the user clicks within the edit box, change the item being added
                    if (mouse_x >= 0 and mouse_x < View.EDIT_BOX_WIDTH) and (mouse_y >= 0 and mouse_y < View.EDIT_BOX_HEIGHT):
                        self.model.update_item_index()

                    # If the user clicks anywhere else, add the corresponding item
                    # Don't add the item if it collides with an existing sprite
                    sprite = None
                    if self.model.items_can_add[self.model.item_index].is_tree():
                        # Adjust Tree to the nearest 75x75 grid
                        x = (mouse_x + self.view.shift_x) // Tree.WIDTH * Tree.HEIGHT
                        y = (mouse_y + self.view.shift_y) // Tree.WIDTH * Tree.HEIGHT
                        sprite = Tree(x, y)
                    elif self.model.items_can_add[self.model.item_index].is_treasure_chest():
                        x = mouse_x + self.view.shift_x
                        y = mouse_y + self.view.shift_y
                        sprite = TreasureChest(x, y)
                    elif self.model.items_can_add[self.model.item_index].is_cucco():
                        x = mouse_x + self.view.shift_x
                        y = mouse_y + self.view.shift_y
                        sprite = Cucco(x, y)
                    
                    if sprite is not None and not(self.model.image_collides(sprite)):
                        self.model.to_add.append(sprite)
            elif event.type == pygame.KEYUP:
                # c - Clear the map
                if event.key == K_c:
                    self.model.clear_map()
                    print("Map cleared and game reset")
                # e - Enter edit mode
                elif event.key == K_e:
                    Controller.edit_mode = not Controller.edit_mode
                # l - load map
                elif event.key == K_l:
                    self.model.load_map()
                    print("Map loaded")
                # s - save map
                elif event.key == K_s:
                    self.model.save_map()
                    print("Map saved")
                elif event.key == K_SPACE:
                    self.key_space = False

        # Save Link's position in the Cucco class
        Cucco.link_x = self.model.link.x
        Cucco.link_y = self.model.link.y

print("Use the arrow keys to move. Press Esc to quit.")
pygame.init()
pygame.font.init()
m = Model()
v = View(m)
c = Controller(m, v)
while c.keep_going:
    c.update()
    m.update()
    v.update()
    sleep(0.04)
print("Goodbye!")