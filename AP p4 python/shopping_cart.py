import errors
import item
from item import Item


class ShoppingCart:

    def __init__(self):
        self.items = {}
        self.hash_tags = []  # keeps track of the hashtags in this shopping cart for search logics

    def add_item(self, curr_item: Item):
        """
        Add an item to the shopping cart
        :param curr_item: the item you wish to add
        """
        if curr_item.name in self.items:  # item is already in the shopping bag
            raise errors.ItemAlreadyExistsError
        else:
            self.items[curr_item.name] = curr_item  # adds the item to the items dict
            self.hash_tags += curr_item.hashtags  # update the hashtags list of this shopping cart
        pass

    def remove_item(self, item_name: str):
        """
        Remove an item from the shopping cart
        :param item_name: the name of the item you want to remove
        """
        if item_name in self.items:
            for hashtag in self.items[item_name].hashtags:  # go throw the hashtags of the item want to remove
                try:
                    self.hash_tags.remove(hashtag)  # remove them from the hashtag list of the shopping cart
                except ValueError:
                    pass
            del self.items[item_name]  # delete the item with this name from the cart

        else:
            raise errors.ItemNotExistError
        pass

    def get_subtotal(self) -> int:
        """
        Calculate the total price for this cart
        :return: the price for this cart
        """
        return sum(i.price for i in self.items.values()) # generator for all items values sum their price
        pass
