package com.stratonica.util.sync.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class SyncClientBase extends SyncBase {
	
	private String host;
	private int port;

	public SyncClientBase(String host, int port) {
		this.host = host;
		this.port = port;
	}

	protected String getResponse(String msg) {
		try (Socket socket = new Socket(host, port)) {

			OutputStream output = socket.getOutputStream();
			PrintWriter writer = new PrintWriter(output, true);

			writer.println(msg);

			InputStream input = socket.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));

			String response = reader.readLine();

			System.out.println(response);

			socket.close();
			return response;

		} catch (UnknownHostException ex) {

			System.out.println("Server not found: " + ex.getMessage());
			return SyncBase.FAILED_CONNECTION;

		} catch (IOException ex) {

			System.out.println("I/O error: " + ex.getMessage());
			return SyncBase.FAILED_CONNECTION;
		}
	
	}

}
