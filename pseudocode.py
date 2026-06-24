import requests
import pandas as pd
 
 
def decode_secret_message(url: str) -> None:
    doc_id = url.split("/d/")[1].split("/")[0]
    csv_url = f"https://docs.google.com/document/d/{doc_id}/export?format=csv"
 
    df = pd.read_csv(requests.get(csv_url).content.decode())
    grid = df.pivot(index="y", columns="x", values="Character").fillna(" ")
    print(grid.to_string(header=False, index=False))
 
 
if __name__ == "__main__":
    import sys
    decode_secret_message(sys.argv[1])
 