package com.vinod.test;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

@Path("/fileupload")
public class JaxRSFileUploadController {

	@POST
	@Path("/fileupload")
	@Consumes("multipart/form-data")
	@Produces(MediaType.APPLICATION_JSON)
	public Response fileupload(MultipartFormDataInput input) {
		String fileName = "";
		int status = -1;
		Response resp = null;
		try {
			Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
			List<InputPart> inputParts = uploadForm.get("fileUpload");
			for (InputPart inputPart : inputParts) {
				MultivaluedMap<String, String> header = inputPart.getHeaders();
				fileName = getFileName(header);
				File file = new File(fileName);
				InputStream inputStream = inputPart.getBody(InputStream.class, null);
				byte[] bytes = IOUtils.toByteArray(inputStream);
				FileUtils.writeByteArrayToFile(file, bytes);
			}
			System.out.println("File upload completed");
			resp = Response.ok().build();
		} catch (Exception e) {
			e.printStackTrace();
			status = status <= 0 ? 500 : status;
			resp = Response.status(status).header("Warning", e.getMessage()).build();
		}
		return resp;
	}

	private String getFileName(MultivaluedMap<String, String> multivaluedMap) {
		String[] contentDisposition = multivaluedMap.getFirst("Content-Disposition").split(";");
		for (String filename : contentDisposition) {
			if (filename.trim().startsWith("filename")) {
				String[] name = filename.split("=");
				String exactFileName = name[1].trim().replaceAll("\"", "");
				return exactFileName;
			}
		}
		return "temp";
	}
}
