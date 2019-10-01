package com.payasia.web.util;

import java.io.IOException;
import java.io.OutputStream;

public class PayAsiaIOOutputStream extends OutputStream {

	private OutputStream out;
	private volatile boolean isActive = true;

	public PayAsiaIOOutputStream(OutputStream out) {
		this.out = out;
	}

	@Override
	public void close() throws IOException {
		if (isActive) {
			isActive = false; // deactivate
			try {
				out.close();
			} finally {
				out = null;
			}
		}
	}

	@Override
	public void flush() throws IOException {
		if (isActive) {
			out.flush();
		}
		// otherwise do nothing (prevent polluting the stream)
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		if (isActive)
			out.write(b, off, len);
	}

	@Override
	public void write(byte[] b) throws IOException {
		if (isActive)
			out.write(b);
	}

	@Override
	public void write(int b) throws IOException {
		if (isActive)
			out.write(b);
	}

}