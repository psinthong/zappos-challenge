

import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;



import java.io.IOException;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;


public class parser {

	/**
	 * @author Phanwadee Sinthong
	 * @param args
	 * @throws IOException 
	 */
	private static List<int[]>priceCombinations = new ArrayList<int[]>();  //-----array that holds all possible price combination -----//
	private static List<Integer>noItem = new ArrayList<Integer>(); //-------array that holds values that are not present in Zappos inventory list ----//
	private static String key = "a73121520492f88dc3d33daf2103d7574f1a3166"; //-------key to make API calls-----//
	private static Map<Integer, List<String>> mapItem = new HashMap<Integer, List<String>>(); //------- holds list of products associate with their prices ----//
	private static int limit = 10; //-------return entry limit ------//
	
	public static void main(String[] args) throws IOException, InterruptedException, URISyntaxException {
		int price = Integer.parseInt(args[0]); //----first command line input for price 
		int pieces = Integer.parseInt(args[1]); //----- second command line input for number of gifts to give
//		int price = 15;
//		int pieces = 3;
	
		combination(price,pieces,0,new int[pieces]);
		
		for(int[] comb : priceCombinations){
			
			if(limit == 0){
				break;
			}
			System.out.println(Arrays.toString(comb));
			int i =0;
			String[] temp = new String[pieces];
			for(int a : comb){
				if(!mapItem.containsKey(a) && !noItem.contains(a)){
					float aPrice = (float)a;
					  	String result = parseJson("http://api.zappos.com/Search?filters={\"price\":[\""+aPrice+"\"]}&key="+key,(int)aPrice, pieces);
						if(!result.equals("")){
							
							temp[i]=result;
							i++;
						}
						else{
							noItem.add(a);
							break;
						}
				}
				else if(mapItem.containsKey(a)){
					Random rand = new Random(); 
					int value = rand.nextInt(mapItem.get(a).size());
					temp[i]=mapItem.get(a).get(value);
					i++;
				}

					
			}
			if(i == pieces && limit > 0){
				System.out.println(Arrays.toString(temp));
				limit--;
			}
			
		}
		 
}
	//---------------A method to parse json result from a call to Zappos' API --------//
	//---------------The method extracts out product name, price and its url ---------//
	/**
	 * A method to parse json result from a call to Zappos' API
	 *
	 * @param path path to get json value
	 * @param price total price input
	 * @param pieces number of gifts to buy
	 * @return String with products' names, prices and urls
	 */
	public static String parseJson(String path,int price, int pieces) throws IOException{

		List<String> itemList = new ArrayList<String>();
		URL url = new URL(path);
		InputStream is = url.openStream();
		JsonReader rdr = Json.createReader(is); 
		JsonObject obj = rdr.readObject();
		JsonArray results = obj.getJsonArray("results");

		if(!results.isEmpty()){
			for(int counter =0; counter < results.size(); counter++){
				String itemName = results.getJsonObject(counter).getString("productName", "");
				String itemPrice = results.getJsonObject(counter).getString("price", "");
				String itemLink = results.getJsonObject(counter).getString("productUrl", "");
				itemList.add(itemName+":"+itemPrice+":"+itemLink);

			}
			mapItem.put(price, itemList);
			Random rand = new Random(); 
			int value = rand.nextInt(itemList.size());
			return 	itemList.get(value);
		}
			return "";
		
	}
	
	//------------ Method to generate possible combination for a given price -------//
	/**
	 * A method to generate all possible combination of prices that adds up to the input price
	 *
	 * @param target the total input price
	 * @param n number of pieces a customer is willing to buy
	 * @param sum current sum of all prices
	 * @param result all possible combination of peices 
	 */
	public static void combination(int target, int n, int sum,int[]result){ 

		if(n==1){

			result[0]=target-sum;
			int[]r =result;
			priceCombinations.add(result.clone());

		}
		else{
			for(int j =1; j<target-sum; j++){
				result[n-1] = j;
				combination(target, n-1,sum+j,result);
			}
		}
		if(target == sum){
			System.out.println("Done");
		}
	}

}
