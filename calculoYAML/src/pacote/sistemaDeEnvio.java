package pacote;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;

import java.io.InputStreamReader;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.ParserConfigurationException;

import org.yaml.snakeyaml.Yaml;


public class sistemaDeEnvio {
	public static String conectarHTTP(String a, String b, int Funcao) {
		String result="";
		try {
			
			URL url = new URL("https://double-nirvana-273602.appspot.com/?hl=pt-BR");
		       HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
		        conn.setReadTimeout(10000);
		        conn.setConnectTimeout(15000);
		        conn.setRequestMethod("POST");
		        conn.setDoInput(true);
		        conn.setDoOutput(true) ;
		        //ENVIO DOS PARAMETROS
		        OutputStream os = conn.getOutputStream();
		        BufferedWriter writer = new BufferedWriter(
		                new OutputStreamWriter(os, "UTF-8"));
		        writer.write("oper1="+a+"&oper2="+b+"&"+"operacao="+Funcao); //1-somar 2-subtrair 3-multiplicar 4-dividir
		        writer.flush();
		        writer.close();
		        os.close();
		        int responseCode=conn.getResponseCode();
		        if (responseCode == HttpsURLConnection.HTTP_OK) {
		            //RECBIMENTO DOS PARAMETROS
		            BufferedReader br = new BufferedReader(
		                    new InputStreamReader(conn.getInputStream(), "utf-8"));
		            StringBuilder response = new StringBuilder();
		            String responseLine = null;
		            while ((responseLine = br.readLine()) != null) {
		                response.append(responseLine.trim());
		            }
		            result = response.toString();
		            //System.out.println(result);
		        }
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		return result;	
	}
	
	public static void main(String[] args) throws ParserConfigurationException, IOException {
		////////////////////////////////////parte do http///////////////////////////////////////////
	       
		//////////////////////////////////////////lendo o YAML////////////////////////////////
      
        InputStream inputStream = new FileInputStream(new File("calculo.yml"));
        Yaml yaml = new Yaml();
        Map<String, Object> obj = yaml.load(inputStream);
        String ExpressaoS = (String) obj.get("calculo");
        
        /////////////////////////////////////////////Dividindo numeros e operadores///////////////////////////////////////////////
        String[] elementosDaExpressao = new String[ExpressaoS.length()];//agrupador de chars para casos de n�meros
        int contadorElemento = 0; //qual o grupo atual
        for(int i = 0; i < ExpressaoS.length(); i++) {
        	if (ExpressaoS.charAt(i)=='0' | ExpressaoS.charAt(i)=='1'| ExpressaoS.charAt(i)=='2'
        		| ExpressaoS.charAt(i)=='3'| ExpressaoS.charAt(i)=='4'| ExpressaoS.charAt(i)=='5'
        		| ExpressaoS.charAt(i)=='6'| ExpressaoS.charAt(i)=='7'| ExpressaoS.charAt(i)=='8'
        		| ExpressaoS.charAt(i)=='9') {//verifica se � um numero
        		if(elementosDaExpressao[contadorElemento]==null) {
        			elementosDaExpressao[contadorElemento] = String.valueOf(ExpressaoS.charAt(i));
        		}
        		else {
        			elementosDaExpressao[contadorElemento]= elementosDaExpressao[contadorElemento] + ExpressaoS.charAt(i);
        		}
        	}
        	else if(ExpressaoS.charAt(i)=='+' |ExpressaoS.charAt(i)=='-' |
        			ExpressaoS.charAt(i)=='*' |ExpressaoS.charAt(i)=='/' ) {//verifica se � um operador
        		contadorElemento++;//passa o elemento
        		elementosDaExpressao[contadorElemento]=String.valueOf(ExpressaoS.charAt(i));//registra operador
        		contadorElemento++;//passa elemento
        	}
        }
        
       // String[] arrayOperandos = new String[ExpressaoS.length()];
        //String[] arrayOperadores = new String[ExpressaoS.length()];
        
        for(int i = 0; i<ExpressaoS.length(); i++) {
        	if(elementosDaExpressao[i].equals("*")) {
        		elementosDaExpressao[i-1]= conectarHTTP(elementosDaExpressao[i-1],elementosDaExpressao[i+1],3);
        		for(int k = i; k<ExpressaoS.length()-2; k++) {
        			
        			elementosDaExpressao[k]=elementosDaExpressao[k+2];
        			elementosDaExpressao[k+2]="";
        		}
        	i = 0;
        	}
        }
        for(int i = 0; i<ExpressaoS.length(); i++) {
	        	if(elementosDaExpressao[i].equals("/")) {
	        		elementosDaExpressao[i-1]= conectarHTTP(elementosDaExpressao[i-1],elementosDaExpressao[i+1],4);
	        		for(int k = i; k<ExpressaoS.length()-2; k++) {
	        			elementosDaExpressao[k]=elementosDaExpressao[k+2];
	        			elementosDaExpressao[k+2]="";
	        		}
	        		i = 0;
	        	}
        }
        for(int i = 0; i<ExpressaoS.length(); i++) {
        	if(elementosDaExpressao[i].equals("+")) {
        		elementosDaExpressao[i-1]= conectarHTTP(elementosDaExpressao[i-1],elementosDaExpressao[i+1],1);
        		for(int k = i; k<ExpressaoS.length()-2; k++) {
        			elementosDaExpressao[k]=elementosDaExpressao[k+2];
        			elementosDaExpressao[k+2]="";
        		}
        		i = 0;
        	}
        }
        for(int i = 0; i<ExpressaoS.length(); i++) {
        	if(elementosDaExpressao[i].equals("-")) { 
        		elementosDaExpressao[i-1]= conectarHTTP(elementosDaExpressao[i-1],elementosDaExpressao[i+1],2);
        		for(int k = i; k<ExpressaoS.length()-2; k++) {
        			elementosDaExpressao[k]=elementosDaExpressao[k+2];
        			elementosDaExpressao[k+2]="";
        		}
        		i = 0;
        	}
        }
        System.out.println(elementosDaExpressao[0]);

        
        }
}