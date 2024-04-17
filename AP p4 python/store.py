import yaml

import errors
from item import Item
from shopping_cart import ShoppingCart


class Store:
    def __init__(self, path):
        with open(path) as inventory:
            items_raw = yaml.load(inventory, Loader=yaml.FullLoader)['items']
        self._items = self._convert_to_item_objects(items_raw)
        self._shopping_cart = ShoppingCart()

    @staticmethod
    def _convert_to_item_objects(items_raw):
        return [Item(item['name'],
                     int(item['price']),
                     item['hashtags'],
                     item['description'])
                for item in items_raw]

    def get_items(self) -> list:
        return self._items

    def search_by_name(self, item_name: str) -> list:
        """
        Search for an item with a given name in the store
        you will only get items that is not in your shopping cart
        can also get part of items name and find all the matching items
        :param item_name: the name of the item tou look for
        :return: list of all matching items
        """
        #  go throw all the items in the store,
        #  add to list1 all items with matching names (can be partial) that are not already in the cart
        list1 = \
            [item for item in self._items if item_name in item.name and item.name not in self._shopping_cart.items]
        # sort according to hashtags (reversed) and secondary by lexicography (given in item class)
        return sorted(list1, key=lambda item: (-self.count_hashtags(item), item))
        pass

    def count_hashtags(self, item: Item) -> int:
        """
        helper function to count the numbers of matching hashtag between an item and the shopping cart
        :param item: item to count the hashtags
        :return: the number of matching hashtags
        """
        # for each hashtag of this item, count the matching hashtags in the shopping cat and sum all
        sum1 = sum(self._shopping_cart.hash_tags.count(hashtag) for hashtag in item.hashtags)
        return sum1

    def search_by_hashtag(self, hashtag: str) -> list:
        """
        search for items using hashtag
        :param hashtag: the hashtag to search with
        :return: list of all matching items
        """
        #  go throw all the items in the store,
        #  add to list1 all items with matching hashtags that are not already in the cart
        list1 = \
            [item for item in self._items if hashtag in item.hashtags and item.name not in self._shopping_cart.items]
        # sort according to hashtags (reversed) and secondary by lexicography (given in item class)
        return sorted(list1, key=lambda item: (-self.count_hashtags(item), item))
        pass

    def add_item(self, item_name: str):
        """
        add an item from the store to the shopping cart
        :param item_name: the name of the item to add
        """
        # list of all matching items
        matching_items = [item for item in self._items if item_name in item.name]
        if len(matching_items) > 1:  # more than 1 matching items
            raise errors.TooManyMatchesError
        if len(matching_items) < 1:  # 0 matching items
            raise errors.ItemNotExistError
        else:
            self._shopping_cart.add_item(matching_items[0])  # try to add the item to the shopping cart
        pass

    def remove_item(self, item_name: str):
        """
        remove an item from the shopping cart
        :param item_name: the item to remove
        """
        # list of all matching items in the shopping cart
        matching_items = [self._shopping_cart.items[name] for name in self._shopping_cart.items if item_name in name]
        if len(matching_items) > 1:  # more than 1 matching items
            raise errors.TooManyMatchesError
        if len(matching_items) < 1:  # 0 matching items
            raise errors.ItemNotExistError
        else:
            self._shopping_cart.remove_item(matching_items[0].name)  # try to remove the item using its name
        pass

    def checkout(self) -> int:
        """
        checkout the shopping cart
        :return: the final price of the items in the shopping cart
        """
        return self._shopping_cart.get_subtotal()
        pass
