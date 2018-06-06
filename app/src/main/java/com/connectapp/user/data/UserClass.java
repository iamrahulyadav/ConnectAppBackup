package com.connectapp.user.data;

import java.io.Serializable;
import java.util.HashMap;

public class UserClass implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String name = "";
	private String userId = "";
	private String databaseId = "";
	private String phone = "";
	private String email = "";
	private String status = "";
	private String userName = "";
	private String password = "";
	private boolean isRemember = false;
	private boolean isLoggedin = false;
	private String message = "";
	private boolean isFirstTimeAccess = true;
	private int currentMemebersDirVersion = 0;
	private int currentMemberCount = 0;
	private int currentCityIndex = -1;
	private int totalMemberCount = 0;
	private int totalCityCount = 0;
	private boolean isMembersDirectoryComplete = false;
	private HashMap<String, String> cityName = new HashMap<String, String>();

	public String firebaseId="";
	public String adminFirebaseId="";
	public String firebaseInstanceId="";


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getDatabaseId() {
		return databaseId;
	}

	public void setDatabaseId(String databaseId) {
		this.databaseId = databaseId;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean getIsRemember() {
		return isRemember;
	}

	public void setIsRemember(boolean isRemember) {
		this.isRemember = isRemember;
	}

	public boolean getIsLoggedin() {
		return isLoggedin;
	}

	public void setIsLoggedin(boolean isLoggedin) {
		this.isLoggedin = isLoggedin;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean getIsFirstTimeAccess() {
		return isFirstTimeAccess;
	}

	public void setIsFirstTimeAccess(boolean isFirstTimeAccess) {
		this.isFirstTimeAccess = isFirstTimeAccess;
	}

	public int getCurrentMemebersDirVersion() {
		return currentMemebersDirVersion;
	}

	public void setCurrentMemebersDirVersion(int currentMemebersDirVersion) {
		this.currentMemebersDirVersion = currentMemebersDirVersion;
	}

	public int getCurrentMemberCount() {
		return currentMemberCount;
	}

	public void setCurrentMemberCount(int currentMemberCount) {
		this.currentMemberCount = currentMemberCount;
	}

	public int getCurrentCityIndex() {
		return currentCityIndex;
	}

	public void setCurrentCityIndex(int currentCityIndex) {
		this.currentCityIndex = currentCityIndex;
	}

	public int getTotalMemberCount() {
		return totalMemberCount;
	}

	public void setTotalMemberCount(int totalMemberCount) {
		this.totalMemberCount = totalMemberCount;
	}

	public int getTotalCityCount() {
		return totalCityCount;
	}

	public void setTotalCityCount(int totalCityCount) {
		this.totalCityCount = totalCityCount;
	}

	public boolean getIsMembersDirectoryComplete() {
		return isMembersDirectoryComplete;
	}

	public void setIsMembersDirectoryComplete(boolean isMembersDirectoryComplete) {
		this.isMembersDirectoryComplete = isMembersDirectoryComplete;
	}

	public HashMap<String, String> getCityName() {
		return cityName;
	}

	public void setCityName(HashMap<String, String> cityName) {
		this.cityName = cityName;
	}

}
