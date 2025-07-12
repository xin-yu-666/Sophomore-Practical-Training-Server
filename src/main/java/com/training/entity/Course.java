package com.training.entity;

import java.util.Date;

public class Course {
	// 主键
	private Integer id;
	// 课程名称
	private String name;
	// 课程简介
	private String summary;
	// 课程封面
	private String cover;
	// 课程排序
	private Integer orderNum;
	// 课程视频
	private String video;
	// 课程作者
	private String author;
	// 创建时间
	private Date createTime;
	// 更新时间
	private Date updateTime;
	// 审核状态
	private Integer status;
	// 课程内容
	private String content;
	// 用户ID
	private Integer userId;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getCover() {
		return cover;
	}

	public void setCover(String cover) {
		this.cover = cover;
	}

	public Integer getOrderNum() {
		return orderNum;
	}

	public void setOrderNum(Integer orderNum) {
		this.orderNum = orderNum;
	}

	public String getVideo() {
		return video;
	}

	public void setVideo(String video) {
		this.video = video;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}
}
