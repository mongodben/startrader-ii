from source_data import species, ship_types, starships, transactions, users
import json
import datetime
from bson import ObjectId
# TODO
# - [ ] combine species with user documents
# - [ ] combine ship_types with starships documents
# - [ ] don't do anything with transactions
# - [ ] export species, ships, transactions to separate JSON files


# user stuff
def find_element_by_id(id, elements, key):
    element = next((x for x in elements if x["id"] == id), None)
    return element[key] if element else None


def create_user_document(user, id, species):
    user_species = find_element_by_id(user["species"], species, "species_type")
    return {
        "_id": id,
        "name": user["name"],
        "email": user["email"],
        "password": user["password"],
        "species": user_species,
        "bio": user["bio"],
    }


def create_starship_type_document(starship_type):
    return {
        "_id": int_to_objectid(starship_type["id"]),
        "type_name": starship_type["type_name"],
        "starship_class": starship_type["starship_class"],
        "manufacturer": starship_type["manufacturer"],
        "model": starship_type["model"],
        "hyperdrive_rating": starship_type["hyperdrive_rating"],
        "mglt": starship_type["mglt"],
        "length": starship_type["length"],
        "crew": starship_type["crew"],
        "passenger": starship_type["passenger"],
        "cargo": starship_type["cargo"],
        "consumables": starship_type["consumables"],
        "cost_credits": starship_type["cost_credits"],
        "ship_image": starship_type["ship_image"],
        "unique": starship_type["unique"]
    }


{'ship_type': 10, 'custom_name': None, 'sale_price': 1000000, 'lightyears_traveled': 24999, 'owner': 14, 'for_sale': True,
 'seller_comment': "The ship that made the Kessel Run in 12 parsecs!", 'post_date': datetime.datetime.now()},


def create_starship_document(starship, id):
    return {
        "_id": id,
        "ship_type_id": int_to_objectid(starship["ship_type"]),
        "custom_name": starship["custom_name"],
        "sale_price": starship["sale_price"],
        "lightyears_traveled": starship["lightyears_traveled"],
        "owner_user_id": int_to_objectid(starship["owner"]),
        "for_sale": starship["for_sale"],
        "seller_comment": starship["seller_comment"],
        "post_date": starship["post_date"],
    }


def create_transaction_document(transaction):
    return {
        "buyer_user_id": int_to_objectid(transaction["buyer"]),
        "seller_user_id": int_to_objectid(transaction["seller"]),
        "starship_id": int_to_objectid(transaction["starship"]),
        "sale_date": transaction["sale_date"],
        "sale_price": transaction["sale_price"],
    }


def convert_data_to_json(data, file_name):
    json_data = json.dumps(data, indent=4, cls=DateTimeEncoder)
    with open(file_name, "w") as f:
        f.write(json_data)


def int_to_objectid(num):
    # Convert integer to a hex string and pad with zeros
    hex_str = hex(num)[2:].rjust(24, '0')
    # Convert hex string to a bytes object and create ObjectId
    return str(ObjectId(bytes.fromhex(hex_str)))


def main():
    # create user documents
    user_documents = []
    for index, user in enumerate(users):
        id = int_to_objectid(index + 1)
        user_documents.append(create_user_document(user, id, species))

    # create starship documents
    starship_documents = []
    for index, starship in enumerate(starships):
        id = int_to_objectid(index + 1)
        starship_documents.append(
            create_starship_document(starship, id)
        )

    transaction_documents = [create_transaction_document(
        t) for t in transactions]

    ship_types_documents = [create_starship_type_document(
        st) for st in ship_types]

    # write to JSON files
    convert_data_to_json(user_documents, "data/users.json")
    convert_data_to_json(starship_documents, "data/starships.json")
    convert_data_to_json(transaction_documents, "data/transactions.json")
    convert_data_to_json(ship_types_documents, "data/ship_types.json")


class DateTimeEncoder(json.JSONEncoder):
    def default(self, obj):
        if isinstance(obj, datetime):
            return obj.isoformat()
        return json.JSONEncoder.default(self, obj)


main()
