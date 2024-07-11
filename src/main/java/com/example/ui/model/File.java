package com.example.ui.model;

public class File
{
    private String id_file;
    private String id_user_upload;
    private String filename;
    private String filepath;
    private String filetype;
    private String upload_date;
    private String filesize;

    public String getUpload_date()
    {
        return upload_date;
    }

    public void setUpload_date(String upload_date)
    {
        this.upload_date = upload_date;
    }

    public String getFilesize()
    {
        return filesize;
    }

    public void setFilesize(String filesize)
    {
        this.filesize = filesize;
    }

    public String getId_file()
    {
        return id_file;
    }

    public void setId_file(String id_file)
    {
        this.id_file = id_file;
    }

    public String getId_user_upload()
    {
        return id_user_upload;
    }

    public void setId_user_upload(String id_user_upload)
    {
        this.id_user_upload = id_user_upload;
    }

    public String getFilename()
    {
        return filename;
    }

    public void setFilename(String filename)
    {
        this.filename = filename;
    }

    public String getFilepath()
    {
        return filepath;
    }

    public void setFilepath(String filepath)
    {
        this.filepath = filepath;
    }

    public String getFiletype()
    {
        return filetype;
    }

    public void setFiletype(String filetype)
    {
        this.filetype = filetype;
    }


    public File(String id_file, String id_user_upload, String filename, String filepath, String filetype, String upload_date, String filesize)
    {
        this.id_file = id_file;
        this.id_user_upload = id_user_upload;
        this.filename = filename;
        this.filepath = filepath;
        this.filetype = filetype;
        this.upload_date = upload_date;
        this.filesize = filesize;
    }
}
