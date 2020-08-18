package chu.sample.ncloud.utils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.MediaType;
//import org.springframework.web.client.RestTemplate;

import chu.sample.ncloud.domain.Messages;
import chu.sample.ncloud.domain.ReqSmsVO;

public class NaverSmsUtilV2 {

	private static final Logger logger = LoggerFactory.getLogger(NaverSmsUtilV2.class);

	private static NaverSmsUtilV2 naverSmsUtils;
//	private static RestTemplate restTemplate;

	private String serviceID;
	private String accessKey;
	private String timestamp;
	private String securityKey;
	private String signitureV2;

	private NaverSmsUtilV2() {
		// TODO Auto-generated constructor stub
	}

	public static NaverSmsUtilV2 getInstance(String serviceID, String accessKey, String securityKey) {

		if (naverSmsUtils == null) {
			naverSmsUtils = new NaverSmsUtilV2();
		}

		naverSmsUtils.serviceID = serviceID;
		naverSmsUtils.accessKey = accessKey;
		naverSmsUtils.securityKey = securityKey;
		
		//SMS 발송시 TimeStamp를 이용하여 전송시간 체크를 한다.
		
		naverSmsUtils.timestamp = makeTimeStamp();
		naverSmsUtils.signitureV2 = makeSignature();

		logger.debug(" signitureV2 : {}", naverSmsUtils.signitureV2);

		return naverSmsUtils;

	}

	private static String makeSignature() {
		String encodeBase64String = "";
		String space = " "; // one space
		String newLine = "\n"; // new line
		String method = "POST"; // method
		String url = String.format("/sms/v2/services/%s/messages", naverSmsUtils.serviceID); // url (include query
																								// string)

		String message = new StringBuilder().append(method).append(space).append(url).append(newLine)
				.append(naverSmsUtils.timestamp).append(newLine).append(naverSmsUtils.accessKey).toString();
		try {

			SecretKeySpec signingKey = new SecretKeySpec(naverSmsUtils.securityKey.getBytes("UTF-8"), "HmacSHA256");
			Mac mac = Mac.getInstance("HmacSHA256");
			mac.init(signingKey);

			byte[] rawHmac = mac.doFinal(message.getBytes("UTF-8"));
//			encodeBase64String = Base64.encodeBase64String(rawHmac);
			encodeBase64String = Base64.getEncoder().encodeToString(rawHmac);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return encodeBase64String;
	}

	/**
	 *
	 * @param sender 발신자 전화번호
	 * @param contents 본문 내용
	 * @param receiverList 수신자 전화번호 리스트
	 * @return
	 */
	public static String sendMessage(String sender, String contents, ArrayList<String> receiverList) {

		String URL = String.format("https://sens.apigw.ntruss.com/sms/v2/services/%s/messages",
				naverSmsUtils.serviceID);

		ReqSmsVO smsVO = new ReqSmsVO();
		smsVO.setType(ReqSmsVO.MESSAGE_TYPE.SMS);
		smsVO.setFrom(sender);
		smsVO.setContent(contents);

		ArrayList<Messages> messages = new ArrayList<>();
		for (String to : receiverList) {
			Messages receiver = new Messages();
			receiver.setTo(to);
			messages.add(receiver);
		}

		smsVO.setMessages(messages);
		
		Client client = ClientBuilder.newClient();
        Response res = client.target(URL)
        		.request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8")
                .header("x-ncp-iam-access-key", naverSmsUtils.accessKey)
                .header("x-ncp-apigw-timestamp", naverSmsUtils.timestamp)
                .header("x-ncp-apigw-signature-v2", naverSmsUtils.signitureV2)
                .post(Entity.entity(smsVO, MediaType.APPLICATION_JSON));
        
        return res.readEntity(String.class);
        
//		restTemplate = new RestTemplate();
//		try {
//			HttpHeaders headers = new HttpHeaders();
//			headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
//			headers.add("x-ncp-iam-access-key", naverSmsUtils.accessKey);
//			headers.add("x-ncp-apigw-timestamp", naverSmsUtils.timestamp);
//			headers.add("x-ncp-apigw-signature-v2", naverSmsUtils.signitureV2);
//
//			ReqSmsVO smsVO = new ReqSmsVO();
//			smsVO.setType(ReqSmsVO.MESSAGE_TYPE.SMS);
//			smsVO.setFrom(sender);
//			smsVO.setContent(contents);
//
//			ArrayList<Messages> messages = new ArrayList<>();
//			for (String to : receiverList) {
//				Messages receiver = new Messages();
//				receiver.setTo(to);
//				messages.add(receiver);
//			}
//
//			smsVO.setMessages(messages);
//
//			logger.debug("body : {}", JsonUtils.serialize(smsVO,true));
//
//			HttpEntity<Object> request;
//
//			request = new HttpEntity<Object>(JsonUtils.serialize(smsVO).getBytes("UTF8"), headers);
//			Object returnVal = restTemplate.exchange(URL, HttpMethod.POST, request, Object.class);
//
//			logger.debug(JsonUtils.serialize(returnVal,true));
//
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

	}
	
//	public static void sendMessageLms(String sender, String subject ,String contents, ArrayList<String> receiverList) {
//
//		String URL = String.format("https://sens.apigw.ntruss.com/sms/v2/services/%s/messages",
//				naverSmsUtils.serviceID);
//
//		restTemplate = new RestTemplate();
//		try {
//			HttpHeaders headers = new HttpHeaders();
//			headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
//			headers.add("x-ncp-iam-access-key", naverSmsUtils.accessKey);
//			headers.add("x-ncp-apigw-timestamp", naverSmsUtils.timestamp);
//			headers.add("x-ncp-apigw-signature-v2", naverSmsUtils.signitureV2);
//
//			ReqSmsVO smsVO = new ReqSmsVO();
//			smsVO.setType(ReqSmsVO.MESSAGE_TYPE.LMS);
//			smsVO.setFrom(sender);
//			smsVO.setSubject(subject);
//			smsVO.setContent(contents);
//
//			ArrayList<Messages> messages = new ArrayList<>();
//			for (String to : receiverList) {
//				Messages receiver = new Messages();
//				receiver.setTo(to);
//				messages.add(receiver);
//			}
//
//			smsVO.setMessages(messages);
//
//			logger.debug("body : {}", JsonUtils.serialize(smsVO,true));
//
//			HttpEntity<Object> request;
//
//			request = new HttpEntity<Object>(JsonUtils.serialize(smsVO).getBytes("UTF8"), headers);
//			Object returnVal = restTemplate.exchange(URL, HttpMethod.POST, request, Object.class);
//
//			logger.debug(JsonUtils.serialize(returnVal,true));
//
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	}

	private static String makeTimeStamp() {
		return String.valueOf(new Date().getTime());
	}

}
