package com.exceedgulf.alainzoo.models;

public class LoginResponseModel {

private String status;
private Messages messages;
private Data data;

public String getStatus() {
return status;
}

public void setStatus(String status) {
this.status = status;
}

public Messages getMessages() {
return messages;
}

public void setMessages(Messages messages) {
this.messages = messages;
}

public Data getData() {
return data;
}

public void setData(Data data) {
this.data = data;
}

}