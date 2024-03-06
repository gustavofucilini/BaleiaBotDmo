package com.br.firesa.baleiaBotDmo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;

import com.twocaptcha.TwoCaptcha;
import com.twocaptcha.captcha.ReCaptcha;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.web.WebEngine;

@Controller
public class BaleiaBotController implements Initializable {

	@FXML
	private Button buttonFubocar;
	
    @FXML
    private Button buttonStop;

	@FXML
	private TextField proxy;

	@FXML
	private TextField quantConta;

	@FXML
	private ProgressBar progresso;

	private WebEngine engine;
	// Register Things
	private List<String> userid = new ArrayList<String>();
	private List<String> emails = new ArrayList<String>();
	private List<String> passwords = new ArrayList<String>();
	private List<LocalDate> aniversarioDatas = new ArrayList<LocalDate>();
	// TempEmail Things
	private List<String> logins = new ArrayList<String>();
	private List<String> domains = new ArrayList<String>();

	private static final String FILE_PATH = System.getProperty("user.home") + File.separator + "Desktop"
			+ File.separator + "logins.csv";

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		try {
			buttonFubocar.setOnAction(event -> {
				buttonFubocar.setDisable(true);
				userid = new ArrayList<String>();
				emails = new ArrayList<String>();
				passwords = new ArrayList<String>();
				aniversarioDatas = new ArrayList<LocalDate>();
				// TempEmail Things
				logins = new ArrayList<String>();
				domains = new ArrayList<String>();
				
				String valorQtdeConta = quantConta.getText();
				int qtdEmails = -1;
				// Executar JavaScript para clicar no botão
				if ("0".equals(valorQtdeConta)) {
				    qtdEmails = 999999999;
				} else if (isNumeric(valorQtdeConta)) {
				    qtdEmails = Integer.parseInt(quantConta.getText());
				} else {
				    JOptionPane.showMessageDialog(null, "Valor Inválido no Campo quantidade de contas");
				    buttonFubocar.cancelButtonProperty();
				    buttonFubocar.setDisable(false);
				}

				try {
					for (int i = 0; i < qtdEmails; i++) {
						final double progress = (double) i + 1 / qtdEmails;
						Platform.runLater(() -> progresso.setProgress(progress));
						Integer emailMessageId;
						URL urlRegisterEmail = new URL("https://dmo.gameking.com/Sign/AjaxSignUpEmailSend.aspx");

						// Gerar as informasões para cadastro
						URL urlGeraEmail = new URL("https://www.1secmail.com/api/v1/?action=genRandomMailbox&count=1");
						String responseUrlGeraEmail = new String();
						responseUrlGeraEmail = requestMails(urlGeraEmail);
						String emailOfResponse = new String();
						emailOfResponse = parseResponse(responseUrlGeraEmail);
						gerarDados(emailOfResponse, i);
						try {
							String urlWithParameters = urlRegisterEmail + "?email="
									+ URLEncoder.encode(emails.get(i), StandardCharsets.UTF_8);
							System.out.println(urlWithParameters);
							String respostaPost = requestGetWithParameters(new URL(urlWithParameters));
							// System.out.println("enviado email registro:");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						try {
							URL registerUrl = null;
							URL urlVerInboxUrl = new URL("https://www.1secmail.com/api/v1/?action=getMessages&login="
									+ logins.get(i) + "&domain=" + domains.get(i));
							System.out.println(urlVerInboxUrl.toString());

							JSONObject responseUrlVerInboxUrl = requestGet(urlVerInboxUrl);
							boolean responseIsBad = true;
							try {
								responseIsBad = false;
								System.out.println(responseUrlVerInboxUrl.toString());
							} catch (NullPointerException npe) {
								responseIsBad = true;
							}
							while (responseIsBad) {
								// System.out.println("Executing script for index " + responseIsBad);
								try {
									Thread.sleep(1000);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
								responseUrlVerInboxUrl = new JSONObject();
								responseUrlVerInboxUrl = requestGet(urlVerInboxUrl);
								try {
									responseIsBad = false;
									System.out.println(responseUrlVerInboxUrl.toString());
								} catch (NullPointerException e) {
									// TODO: handle exception
									responseIsBad = true;
								}
							}
							// Variant For Json Labels
							System.out.println(responseUrlVerInboxUrl.toString());
							Integer id = responseUrlVerInboxUrl.getInt("id");
							String from = responseUrlVerInboxUrl.getString("from");
							String subject = responseUrlVerInboxUrl.getString("subject");
							// End
							if (subject.contains("GAMEKING.com") || subject.contains("Verification")) {
								if (from.contains("gameking.com")) {
									emailMessageId = id;

									URL urlVerMenssagemUrl = new URL(
											"https://www.1secmail.com/api/v1/?action=readMessage&login=" + logins.get(i)
													+ "&domain=" + domains.get(i) + "&id=" + emailMessageId);
									JSONObject responseVerMenssagemUrl = requestGet(urlVerMenssagemUrl);
									// System.out.println(responseVerMenssagemUrl.getString("body"));
									registerUrl = pegarLinkRegistroHtml(responseVerMenssagemUrl.getString("body"));
									// System.out.println(registerUrl);
									// System.out.println("=====\n"+registerUrl.toString()+"\n=====");

									// Faz uma requisição HTTP para obter o conteúdo da página
									// System.out.println(registerUrl.toString());

									String url = registerUrl.toString();

									Thread.sleep(5000);
									// System.out.println(url);
									String pageContent = getContentFromUrl(url);

									// Extrai o valor de jid usando uma expressão regular simples
									String jid = extractJid(pageContent);

									// Imprime o valor de jid
									// System.out.println("Valor de jid: " + jid);

									String formUrl = "https://dmo.gameking.com/Sign/SignUpComplete.aspx";

									// Dados do formulário
									Map<String, String> data = new HashMap<>();
									// System.out.println(jid);
									data.put("jid", jid);
									data.put("email", emails.get(i));
									data.put("vf", registerUrl.toString()
											.replace("https://dmo.gameking.com/Sign/SignUpWrite.aspx?vf=", ""));

									// Adicione outros campos conforme necessário
									data.put("userid2", userid.get(i));
									data.put("usernick", logins.get(i));
									data.put("userpw", passwords.get(i));
									data.put("userpwchk", passwords.get(i));
									data.put("birthMM", String.format("%02d", aniversarioDatas.get(i).getMonthValue()));
									data.put("birthDD", String.format("%02d", aniversarioDatas.get(i).getDayOfMonth()));
									data.put("birthYYYY", Integer.toString(aniversarioDatas.get(i).getYear()));

									// Adiciona o campo U_checkAgreement1 com o valor "checked"
									data.put("U_checkAgreement1", "");
									// Pegar resposta Captha
									data.put("g-recaptcha-response", captchaSolver(registerUrl.toString()));
									// Executa o POST
									sendPostRequest(formUrl, data);
									adicionarInformacoesAoCSV(userid.get(i), emails.get(i), passwords.get(i),
											aniversarioDatas.get(i), logins.get(i), domains.get(i));

								}
							}
						} catch (MalformedURLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					buttonFubocar.setDisable(false);

				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

			});

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	private void gerarDados(String responseEmail, int index) {
		// for (int i = 0; i < ResponseEmails.length; i++) {
		logins.add(responseEmail.split("@")[0]);
		domains.add(responseEmail.split("@")[1]);
		emails.add(responseEmail);
		userid.add(Generators.generateUser(logins.get(index)));
		passwords.add(Generators.generatePassword());
		aniversarioDatas.add(Generators.generateBirthday());
		// }
	}

	private JSONObject requestGet(URL url) throws IOException {
		Proxy proxy = parseProxy(this.proxy.getText());
		HttpURLConnection conn;
		if (proxy != null) {
			conn = (HttpURLConnection) url.openConnection(proxy);
		} else {
			conn = (HttpURLConnection) url.openConnection();
		}
		conn.setRequestMethod("GET");
		int responseCode = conn.getResponseCode();
		if (responseCode == 200) {
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String inputLine;
			StringBuilder response = new StringBuilder();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			// System.out.println(response.toString().substring(1,
			// response.toString().length() - 1));
			JSONObject jsonResponse = null;

			if (isValid(response.toString())) {
				jsonResponse = new JSONObject(response.toString());
				conn.disconnect();
				return jsonResponse;
			} else if (isValid(response.toString().substring(1, response.toString().length() - 1))) {
				jsonResponse = new JSONObject(response.toString().substring(1, response.toString().length() - 1));
				conn.disconnect();
				return jsonResponse;
			} else {
				jsonResponse = null;
				conn.disconnect();
				return null;
			}
		} else {
			return null;
		}

	}

	private String requestGetWithParameters(URL url) throws IOException {
		Proxy proxy = parseProxy(this.proxy.getText());
		HttpURLConnection conn;
		if (proxy != null) {
			conn = (HttpURLConnection) url.openConnection(proxy);
		} else {
			conn = (HttpURLConnection) url.openConnection();
		}
		conn.setRequestMethod("GET");

		int responseCode = conn.getResponseCode();
		if (responseCode == 200) {
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String inputLine;
			StringBuilder response = new StringBuilder();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			return response.toString();
		} else {
			return null;
		}
	}

	private String requestMails(URL url) throws IOException {
		HttpURLConnection conn;

		// Configuração do proxy, se necessário
		if (proxy != null && !proxy.toString().isEmpty()) {
			Proxy proxy = parseProxy(this.proxy.getText());
			if (proxy != null) {
				conn = (HttpURLConnection) url.openConnection(proxy);
			} else {
				conn = (HttpURLConnection) url.openConnection();
			}
		} else {
			conn = (HttpURLConnection) url.openConnection();
		}

		conn.setRequestMethod("GET");
		int responseCode = conn.getResponseCode();

		if (responseCode == 200) {
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String inputLine;
			StringBuilder response = new StringBuilder();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			conn.disconnect();
			return response.toString();
		} else {
			return null;
		}
	}

	private Proxy parseProxy(String proxyText) {
		if (proxyText.isEmpty()) {
			return null; // Nenhum proxy se o campo estiver vazio
		} else {
			String[] parts = proxyText.split(":");
			String ip = parts[0];
			int porta = Integer.parseInt(parts[1]);

			Proxy proxy;

			if (parts.length == 2) {
				proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, porta));
			} else if (parts.length == 3 || parts.length == 4) {
				String usuario = parts[2];
				String senha = (parts.length == 4) ? parts[3] : ""; // Adiciona senha se presente
				Authenticator.setDefault(new Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(usuario, senha.toCharArray());
					}
				});
				proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, porta));
			} else {
				throw new IllegalArgumentException("Formato de proxy inválido: " + proxyText);
			}

			return proxy;
		}
	}

	private URL pegarLinkRegistroHtml(String body) {
		String regex = "https:\\/\\/dmo.gameking.com\\/Sign\\/SignUpWrite.aspx\\?vf=[a-f0-9]+";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(body);

		while (matcher.find()) {
			String linkWithSlashes = matcher.group();
			URL link;
			try {
				link = new URL(linkWithSlashes.replace("\\", ""));
				return link;
			} catch (MalformedURLException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, e.getMessage());
			}
			return null;
		}

		return null;
	}

	private String captchaSolver(String siteUrl) {
		TwoCaptcha solver = new TwoCaptcha("762a819bf2c83f245edae02f3387cbde");
		ReCaptcha captcha = new ReCaptcha();

		captcha.setSiteKey("6LcJSWUUAAAAAC8T2AZnZHYO9cD2gs6gjVKv4QTm");
		captcha.setUrl(siteUrl);
		captcha.setInvisible(true);
		captcha.setAction("verify");
		try {
			solver.solve(captcha);
			// Wait for the captcha to be solved
			Thread.sleep(5000);
			String captchaResult = solver.getResult(captcha.getId());
			return captchaResult;
			// System.out.println("Captcha result: " + captchaResult);
			/*
			 * engine.executeScript(
			 * "document.addEventListener('DOMContentLoaded', function() {" +
			 * "document.getElementById('g-recaptcha-response').innerHTML = '" +
			 * captchaResult + "';" +
			 * "document.querySelector('#recaptcha-checkbox-checkmark').checked = true;" +
			 * "});");
			 */
			// System.out.println("Script: captcha Executed Successfully");
		} catch (Exception e) {
			System.out.println("Captcha Error occurred: " + e.getMessage());
		}
		return "";
	}

	private static boolean isValid(String json) {
		try {
			new JSONObject(json);
		} catch (JSONException e) {
			return false;
		}
		return true;
	}

	private static void sendPostRequest(String url, Map<String, String> data) {
		// Verifica se todos os valores no mapa são não nulos
		for (Map.Entry<String, String> entry : data.entrySet()) {
			if (entry.getKey() == null) {
				System.out.println("Chave nula encontrada!");
				return;
			}
			if (entry.getValue() == null) {
				System.out.println("Valor nulo encontrado para a chave: " + entry.getKey());
				return;
			}
		}

		// O restante do código permanece o mesmo
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url))
				.header("Content-Type", "application/x-www-form-urlencoded").POST(buildFormDataFromMap(data)).build();

		try {
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

			// Exibe a resposta
			System.out.println("Response Code: " + response.statusCode());
			System.out.println("Response Body: " + response.body());

			// Exibe os headers (opcional)
			HttpHeaders headers = response.headers();
			headers.map().forEach((k, v) -> System.out.println(k + ":" + v));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static HttpRequest.BodyPublisher buildFormDataFromMap(Map<String, String> data) {
		StringBuilder builder = new StringBuilder();
		for (Map.Entry<String, String> entry : data.entrySet()) {
			if (builder.length() > 0) {
				builder.append("&");
			}
			// Verifica se o valor é nulo antes de chamar toString()
			builder.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
			builder.append("=");
			builder.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
		}
		return BodyPublishers.ofString(builder.toString());
	}

	private static String getContentFromUrl(String url) {
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();

		try {
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			return response.body();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static String extractJid(String pageContent) {
		// Use uma expressão regular para encontrar o valor de jid na página
		Pattern pattern = Pattern.compile("name=\"jid\" value=\"(.*?)\"");
		Matcher matcher = pattern.matcher(pageContent);

		// Se encontrar correspondência, retorna o valor de jid
		if (matcher.find()) {
			return matcher.group(1);
		}

		// Retorna nulo se não encontrar
		return null;
	}

	public void adicionarInformacoesAoCSV(String userid, String email, String password, LocalDate aniversarioData, String login, String domain) {
		try (FileWriter fileWriter = new FileWriter(FILE_PATH, true);
				CSVPrinter csvPrinter = new CSVPrinter(fileWriter, CSVFormat.DEFAULT.withDelimiter(';'))) {

			// Se o arquivo estiver vazio, imprima o cabeçalho
			if (isArquivoVazio(FILE_PATH)) {
				List<String> header = Arrays.asList("UserID", "Email", "Password", "AniversarioData", "Login",
						"Domain", "Feito", "Tera");
				csvPrinter.printRecord(header);
			}

			// Adicione as informações ao CSV usando um loop
			List<String> rowData = Arrays.asList(userid, email, password, aniversarioData.toString(), login, domain, "", "");
			csvPrinter.printRecord(rowData);

			System.out.println("Informações adicionadas com sucesso ao arquivo CSV!");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean isArquivoVazio(String filePath) {
		File file = new File(filePath);
		return file.length() == 0;
	}

	public static boolean isNumeric(String str) {
		try {
			Double.parseDouble(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	// ... Outras partes do código ...

	private static String parseResponse(String response) {
		if (response == null) {
			return null;
		}

		// Aqui você pode continuar o seu código para processar a resposta
		// A linha abaixo é um exemplo hipotético, ajuste conforme necessário
		return new String(response.replaceAll("[\"\\[\\]]", ""));
	}

	// ... Outras partes do código ...

}
