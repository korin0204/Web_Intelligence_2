package work3;

//import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.atilika.kuromoji.ipadic.Token;
import com.atilika.kuromoji.ipadic.Tokenizer;

// Work1-2b
class BoW {    
	static Tokenizer tokenizer = new Tokenizer(); // 初期化が遅いので再利用する。
    
	HashMap<String, Integer> termCount = new HashMap<>();
	ABPairList pairList = new ABPairList();
 
	// Work1-2b
	static BoW create(String text) {
		// 1. textを形態素解析する
		List<Token> tokens = tokenizer.tokenize(text);   
        // 2. 不用語を除去する
		BoW A = new BoW(tokens);//BoWオブジェクトの初期化
		//contentWordsでないなら、termCountに追加しない
		String preTerm = ""; //名詞の接続用
		boolean inSubject = false; //名詞の連続確認
		int NumSubject = -1;
		
		String WordA = "";
		String WordB = "";
		boolean isWordA = true;
		
		
		for(Token token: tokens) {			
			if(StopWords.isStopWord(token)) {//もし名詞なら
				inSubject = true;
				String term = BoW.repr(token); //termにtokenのsurfaceを格納
				preTerm = preTerm + term;
				
				
//                A.termCount.put(term, A.termCount.getOrDefault(term, 0));
                
                
			}else { //名詞が連続しなかったら、空文字
//				if(inSubject) {
//					
//					
//					A.termCount.put(preTerm, A.termCount.getOrDefault(preTerm, 0) + NumSubject--); //連続する名詞もBoWとして追加
//					
//					
//					
//					preTerm = ""; 
//				}
				
				
				if(inSubject) {
					if(isWordA) {
						WordA = preTerm; //A
						System.out.println(WordA);
						isWordA = false;
						preTerm = "";
					}else {
						WordB = preTerm;
						System.out.println(WordB);
						isWordA = true;
						preTerm = "";
						A.pairList.addPair(WordA,WordB);
					}
				}
				inSubject = false;
				
			}
		}
		if(inSubject) {
//			A.termCount.put(preTerm, A.termCount.getOrDefault(preTerm, 0) + NumSubject--); //名詞だけで終わっていた時
//			preTerm = "";
			if(isWordA) {
				WordA = preTerm; //A
				System.out.println(WordA);
				isWordA = false;
				preTerm = "";
			}else {
				WordB = preTerm;
				System.out.println(WordB);
				isWordA = true;
				A.pairList.addPair(WordA,WordB);
				preTerm = "";
			}
		}
		
		// 3. BoW を生成する
        return A;
	}

	// Work1-2b
	static BoW fetch(String url) {
		try {
			// 実装してみよう
			// 1. urlからHTMLコンテンツの本文を取得する。
			Document doc = Jsoup.connect(url).get();
			Element articleBody = doc.getElementById("article-body");
			if(articleBody == null) {
				System.out.println( "cannot read article-body " + url);
				articleBody = doc.body();
				if(articleBody == null){
					System.out.println( "cannot read anything" + url);
					return null;
				}
			}
			String text = articleBody.text();
			
			// // 1秒間隔で次の取得を待つ
			// Thread.sleep(1000); // 1000ミリ秒 = 1秒

            return BoW.create(text);
			// 2. BoWクラスを初期化して bow 変数に格納する。
		} catch (Exception e) {
			System.err.println(e);
			return null;
		}
	}
	
	static List<BoW> fetch2(String url) {
		try {
			Document doc = Jsoup.connect(url).get();
            String text = doc.body().text();
            return BoW.createFromSentences(text);
		} catch (Exception e) {
			System.err.println(e);
			return null;
		}
	}
	
    //文を区切ってBoWリストを生成
    static List<BoW> createFromSentences(String text) {
        String[] sentences = text.split("[。 ]");
        List<BoW> bowList = new ArrayList<>();
        for (String sentence : sentences) {
            if (!sentence.trim().isEmpty()) {
                BoW bow = BoW.create(sentence);
                bowList.add(bow);
            }
        }
        return bowList;
    }
		
	// Work1-2b
	BoW(List<Token> tokens) {
		for (Token token: tokens) {
			// 1. 語の表現形式を統一する
			String term = BoW.repr(token);

			// 2. termCount 中の語 term をカウントアップ
		}
	}

	public String toString() {
		return this.termCount.toString();
	}
	
	static String repr(Token token) {
//		return token.getBaseForm();
		return token.getSurface();
	}
	
	int[] toArray(ArrayList<String> indexTerms) {
        int[] array = new int[indexTerms.size()];
        for (int i = 0; i < indexTerms.size(); i++) {
            array[i] = this.termCount.getOrDefault(indexTerms.get(i), 0);
        }
        return array;
    }
	// ユニークな文字の数を返すメソッドを追加
    int getUniqueTermCount() {
        return this.termCount.size();
    }
}

// Work1-2b
// 不要語については各自検討せよ。
class StopWords {
	static HashSet<String> contentWords = new HashSet<>() {{
		add("名詞");
		//add("形容詞");
		//add("カスタム名詞");
	}};
	static HashSet<String> contentWords2 = new HashSet<>() {{
		//add("可愛い");
		//add("クール");
		//add("優しい");
		//add("冷たい");
		//add("天才");
		//add("努力");
	}};
	static HashSet<String> NGWords = new HashSet<>() {{
		add("?");
		add("？");
	}};

	
	static boolean isStopWord(Token token) {
		return (StopWords.contentWords.contains(token.getPartOfSpeechLevel1())
				|| StopWords.contentWords2.contains(token.getSurface())
				//|| StopWords.NGWords.contains(token.getSurface())
				);
	}
}
