package chu.sample.ncloud.domain;

import java.util.ArrayList;

public class ReqSmsVO {
	// 메시지 전송타입 : "(SMS | LMS | MMS)",
	private MESSAGE_TYPE type;
	// 발신자 :
	private String from;
	// 발신내용 :
	private String content;
	// 발신제목 :
	private String subject;
	// 수신자 목록 :
	private ArrayList<Messages> messages = new ArrayList<>();

	public MESSAGE_TYPE getType() {
		return type;
	}

	public void setType(MESSAGE_TYPE type) {
		this.type = type;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public ArrayList<Messages> getMessages() {
		return messages;
	}

	public void setMessages(ArrayList<Messages> messages) {
		this.messages = messages;
	}

	public enum MESSAGE_TYPE {
		SMS("SMS"), LMS("LMS");

		private MESSAGE_TYPE(String type) {
			// TODO Auto-generated constructor stub
			this.type = type;
		}

		private String type;

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return this.type;
		}

	}

}
