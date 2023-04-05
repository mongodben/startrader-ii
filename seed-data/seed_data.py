# Script to seed StarTrader data into MongoDB
import json
import os
import datetime
from dotenv import load_dotenv
import pymongo
import bson

load_dotenv()


MONGODB_CONNECTION_URI = os.getenv("MONGODB_CONNECTION_URI")
DATABASE_NAME = os.getenv("DATABASE_NAME")
client = pymongo.MongoClient(MONGODB_CONNECTION_URI)


files = ["data/users.json", "data/starships.json",
         "data/transactions.json", "data/ship_types.json"]


def load_data_from_json(file_name):
    with open(file_name, "r") as f:
        return json.load(f)


user_data = load_data_from_json(files[0])
starship_data = load_data_from_json(files[1])
transaction_data = load_data_from_json(files[2])
ship_type_data = load_data_from_json(files[3])


def load_mongo_data(data, collection_name):
    collection = client.get_database(
        DATABASE_NAME).get_collection(collection_name)
    # wipe collection data
    collection.delete_many({})
    # insert data into collection with upsert
    collection.insert_many(data)


# Data transformation functions
def transform_user_data(user):
    user["_id"] = bson.ObjectId(user["_id"])
    return user


def transform_starship_data(starship):
    starship["_id"] = bson.ObjectId(starship["_id"])
    starship["ship_type_id"] = bson.ObjectId(starship["ship_type_id"])
    starship["owner_user_id"] = bson.ObjectId(starship["owner_user_id"])
    starship["post_date"] = datetime.datetime.strptime(
        starship["post_date"], "%Y-%m-%dT%H:%M:%S.%f")

    return starship


def transform_transaction_data(transaction):
    transaction["starship_id"] = bson.ObjectId(transaction["starship_id"])
    transaction["buyer_user_id"] = bson.ObjectId(transaction["buyer_user_id"])
    transaction["seller_user_id"] = bson.ObjectId(
        transaction["seller_user_id"])
    transaction["sale_date"] = datetime.datetime.strptime(
        transaction["sale_date"], "%Y-%m-%dT%H:%M:%S.%f")
    return transaction


def transform_ship_type_data(ship_type):
    ship_type["_id"] = bson.ObjectId(ship_type["_id"])
    return ship_type


users_bson = [transform_user_data(user) for user in user_data]
starships_bson = [transform_starship_data(
    starship) for starship in starship_data]
transactions_bson = [transform_transaction_data(
    transaction) for transaction in transaction_data]
ship_types_bson = [transform_ship_type_data(
    ship_type) for ship_type in ship_type_data]

# Load data into MongoDB
load_mongo_data(users_bson, "users")
load_mongo_data(starships_bson, "starships")
load_mongo_data(transactions_bson, "transactions")
load_mongo_data(ship_types_bson, "ship_types")

client.close()
