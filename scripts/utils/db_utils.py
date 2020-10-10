import sqlite3
import getpass

def createDbConn():
    PATH = f"/virtual/{getpass.getuser()}/URLShortner/urlshortner.db"
    conn = sqlite3.connect(PATH)
    return conn

def getDBCount(cursor):
    select = "SELECT COUNT(short) AS count FROM URLSHORTNER"
    for raw in cursor.execute(select):
        return raw[0]
    
if __name__ == "__main__":
    conn = createDbConn()
    cursor = conn.cursor()
    print(getDBCount(cursor))