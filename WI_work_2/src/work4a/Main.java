package work4a;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;


public class Main {

	// Google Knowledge Graph Search APIの認証用API Keyに変える 
	static String gkgsApiKey = "AIzaSyA7fleeSTcApMpa1a3WUptuHgwQkPbV1e8"; 
	
	public static void main(String[] args) throws IOException {
		
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			System.out.print("You > ");
			String userInput = in.readLine();  // 標準入力からのユーザ発話を取得
			
			BoW user_q = BoW.create(userInput); // ユーザーの発話から名詞句を取り出しBoWを作る
			
			
			if (userInput.equals("ばいばい") || userInput.equals("exit")) {
				System.out.println("Bot > ばいばい");
				break;
			} else {
				// Work 2-1 関係のソースコードをコメントアウトしました
				
				System.out.println("知識の検索");
				user_q.pairList.printPairs(); //BoWの中身(A,B)のセットのリストが表示される
				
				
//				// BoWに含まれる各キー（語）をループで表示
//				List<String> userSubjects = user_q.termCount.entrySet().stream()
//						.filter(entry -> entry.getValue() <= -1)
//						.map(HashMap.Entry::getKey)
//						.collect(Collectors.toList());
//				//-(奇数)の-(偶数)は？という形で値が保存
//				//-1の-2や、-3の-4というようになっている
//				//エンティティ-(奇数)のプロパティ-(偶数)のプロパティ値を取得する
				
				for(int i = 0; i < user_q.pairList.getSize(); i++) {
					String termA = user_q.pairList.getAat(i);
					String termB = user_q.pairList.getBat(i);
					//termA,termBには(A,B)という形になっているAが入り、エンティティのプロパティを検索した後、その各プロパティについてプロパティ値を取得する。
					
					
					//termAのプロパティを取得する
		        	System.out.println(termA);
		            System.out.println("Term: " + termA);
		            String wdJsonA = getWikidataJson(termA);
		            
//		            System.out.println("[JSON]\n" + wdJsonA);//A(藤井聡太)のjson
		            
					Map<String, Object> wdMapA = json2Map(wdJsonA);//藤井聡太のjsonをmapにする
//					System.out.println(wdMapA);
					

					//termBのプロパティ値を取得する。
					System.out.println(termB);
		            System.out.println("Term: " + termB);
		            String wdJsonB = getWikidataJsonInB(termB);
		            
//		            System.out.println("[JSON]\n" + wdJsonB);
		            
					Map<String, Object> wdMapB = json2Map(wdJsonB);
					
					//System.out.println("[Map]\n" + wdMapB);
							
					System.out.println("\n----------\n");

					//String propB = "P856";
					List<Map<String, Object>> resListB = (List<Map<String, Object>>) wdMapB.get("result");
					if(resListB.size() == 0) {
						System.out.println("分かりません");
						System.exit(0);
					}
//					for (Map<String, Object> res: resListB) {
//						String entityID = getEntityID(res);
//						List<String> propVals = getPropVals(res, propB);
//						System.out.println("エンティティ "+entityID+"のプロパティ"+propB+": " + propVals);
//					}
					String propID = getEntityID(resListB.get(0));//出身地のプロパティ値を取り出す
					////
					
					
					
					
					
					
					String prop = propID;//ここの値はBから取得したもの
					
					
					List<Map<String, Object>> resList = (List<Map<String, Object>>) wdMapA.get("result");
					//System.out.println(resList);
//					for (Map<String, Object> res: resList) {
//						String entityID = getEntityID(res);//藤井聡太のエンティティID
//						List<String> propVals = getPropVals(res, prop);
//						System.out.println("エンティティ "+entityID+"のプロパティ"+prop+": " + propVals);
//					}
					
					Map<String, Object> res = resList.get(0);
					String entityID = getEntityID(res);//藤井聡太のエンティティID
					List<String> propVals = getPropVals(res, prop);
//					System.out.println("エンティティ "+entityID+"のプロパティ"+prop+": " + propVals);
					
					String botResp = generateResponse(termA, termB, propVals);  // botの返答を生成
					System.out.println("Bot > " + botResp);
				}
		    }
				
				
			}System.exit(0);	
		}

	/**
	 * botの返答を生成して返す
	 * @param userInput
	 * @return 生成したbotの返答
	 */
	public static String generateResponse(String A, String B, List<String> P) {
		return A + "の" + B + "は" + P + "ですよ";
	}
	
	/**
	 * 
	 * @param res
	 * @return
	 */
	public static String getEntityID(Map<String, Object> res) {
		return (String)((Map)res.get("entities")).keySet().iterator().next();
	}
	
	/**
	 * 
	 * @param map
	 * @param prop
	 * @return
	 */
	public static List<String> getPropVals(Map<String, Object> res, String prop) {
		List<String> vals = new ArrayList<String>();
		String entityID = getEntityID(res);
		Map entityMap = (Map)((Map)res.get("entities")).get(entityID);
		Map claimMap = (Map)entityMap.get("claims");
		if (claimMap != null) {	
			List<Map> propList = (List<Map>)claimMap.get(prop);
			if (propList != null) {
				for (Map propMap: propList) {
					Map<String,Object> valMap = (Map)((Map)propMap.get("mainsnak")).get("datavalue");
					Object val = valMap.get("value");
					if(val instanceof String) {
						vals.add((String)val);
					}else if(val instanceof Map) {
						Map map = (Map)val;
						String id = (String)map.get("id");
						
//						System.out.println(id);
						
						String jaLabel = getLabelById(id);
						vals.add(jaLabel);
					}else {
						vals.add(val.toString());
					}
				}
			}
		}	
		return vals;
	}
	
	public static String getLabelById(String id) {
		String wdJson = getWikidataJson(id);
		Map<String, Object> wdMap = json2Map(wdJson);
//		System.out.println(id + wdMap);
		List<Map<String, Object>> resListB = (List<Map<String, Object>>) wdMap.get("result");
		Map<String, Object> result = resListB.get(0);
//		String entityID = getEntityID(resListB.get(0));
		String entityID = getEntityID(result);
		Map<String, Object> entities = (Map<String, Object>) result.get("entities");
		Map<String, Object> entity = (Map<String, Object>) entities.get(entityID);
		Map<String, Object> labels = (Map<String, Object>) entity.get("labels");
		Map<String, Object> jaLabel = (Map<String, Object>) labels.get("ja");
		
		
		return (String) jaLabel.get("value");
	}
	
	/**
	 * Wikidataからデータを検索
	 * @param query
	 * @return　Wikidataから取得したJSON文字列
	 */
	public static String getWikidataJson(String query) {
		StringBuffer sb = new StringBuffer();
		sb.append("{\"result\":[");
		int initLen = sb.length();
		List<String> ids = getWikidataIds(query);
		for (String id: ids) {
			if (sb.length() > initLen) {
				sb.append(",");
			}
			String url = "https://www.wikidata.org/wiki/Special:EntityData/" + id + ".json";
			String json = getData(url);
			sb.append(getData(url));
		}
		sb.append("]}");
		return sb.toString();
	}
	
	/**
	 * Wikidataからデータを検索
	 * @param query
	 * @return　Wikidataから取得したJSON文字列
	 */
	public static String getWikidataJsonInB(String query) {
		StringBuffer sb = new StringBuffer();
		sb.append("{\"result\":[");
		int initLen = sb.length();
		List<String> ids = getWikidataPropIds(query);
		for (String id: ids) {
			if (sb.length() > initLen) {
				sb.append(",");
			}
			String url = "https://www.wikidata.org/wiki/Special:EntityData/" + id + ".json";
			String json = getData(url);
			sb.append(getData(url));
		}
		sb.append("]}");
		return sb.toString();
	}
	
	/**
	 * WikidataエンティティのIDを検索
	 * @param query
	 * @return WikidataエンティティのIDのリスト
	 */
	public static List<String> getWikidataIds(String query) {
		String encodedQuery = "";
		try {
			encodedQuery = URLEncoder.encode(query, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}	
		String url = "https://www.wikidata.org/w/api.php?action=wbsearchentities&language=ja&format=json"
		              + "&search=" + encodedQuery;
		Map<String, Object> map = json2Map(getData(url));
		List<Map<String, Object>> list = (List<Map<String, Object>>)map.get("search");
		List<String> ids = new ArrayList<String>();
		for (Map<String, Object> entMap: list) {
			String id = (String)entMap.get("id");
			ids.add(id);
		}
		return ids;
	}
	
	//プロパティIDを取得するメソッド
	public static List<String> getWikidataPropIds(String query) {
		String encodedQuery = "";
		try {
			encodedQuery = URLEncoder.encode(query, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}	
		String url = "https://www.wikidata.org/w/api.php?action=wbsearchentities&language=ja&format=json&type=property"
		              + "&search=" + encodedQuery;
		Map<String, Object> map = json2Map(getData(url));
		List<Map<String, Object>> list = (List<Map<String, Object>>)map.get("search");
		List<String> ids = new ArrayList<String>();
		for (Map<String, Object> entMap: list) {
			String id = (String)entMap.get("id");
			ids.add(id);
		}
		return ids;
	}
	
	/**
	 * Google Knowledge Graph からデータを検索
	 * @param query
	 * @return Google Knowledge Graphから取得したJSON文字列
	 */
	public static String getGoogleKGJson(String query) {
		String encodedQuery = "";
		try {
			encodedQuery = URLEncoder.encode(query, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}	
		String url = "https://kgsearch.googleapis.com/v1/entities:search?query=" + encodedQuery  
				   + "&languages=ja&key=" + gkgsApiKey;
		return getData(url);
	}
	
	/**
	 * JSON形式の文字列をMapに変換
	 * @param json
	 * @return JSONから変換したMapオブジェクト
	 */
	public static Map<String, Object> json2Map(String json) {
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> map = new HashMap<String, Object>();
		map = null;
		try {
			map = mapper.readValue(json, new TypeReference<Map<String, Object>>(){});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	
	
	/**
	 * HTMLに限らない形式のデータをWebから取得
	 * @param url
	 * @return 返ってきたデータ
	 */
    public static String getData(String url) {
    	String enc = "UTF-8";
    	StringBuffer sb = new StringBuffer();
    	try {
    		BufferedReader in = null;
    		if (url.startsWith("https")) {
    			HttpsURLConnection conn = (HttpsURLConnection)new URL(url).openConnection();
    			in = new BufferedReader(new InputStreamReader(conn.getInputStream(), enc));
    		} else {
    			URLConnection conn = new URL(url).openConnection();
    			in = new BufferedReader(new InputStreamReader(conn.getInputStream(), enc));
    		}
    		for (String line = in.readLine(); line != null; line = in.readLine()) {
    			sb.append(line);
    			sb.append("\n");
    		}
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	return sb.toString();
    }


}

