package models;

import com.google.gson.annotations.SerializedName;
import java.util.Date;

public class ChatSession {
    @SerializedName("_id")
    private String id;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("startTime")
    private Date startTime;
    
    @SerializedName("endTime")
    private Date endTime;
    
    @SerializedName("summary")
    private String summary;

    public String getId() { return id; }
    public String getName() { return name; }
    public Date getStartTime() { return startTime; }
    public Date getEndTime() { return endTime; }
    public String getSummary() { return summary; }
} 