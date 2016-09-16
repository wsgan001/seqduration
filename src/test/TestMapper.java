package test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import parameters.FileAddresses;
import patterninference.Mapper;
import sensor.SensorUtil;

public class TestMapper {

	public static void run() throws ClassNotFoundException, IOException {
		final SensorUtil sensors = TestInferer.getSensors();
		for (int i = 0; i < 10; i++) {
			Mapper m = new Mapper(sensors, FileAddresses.RAW_TRAIN_MAP_SQ + i, FileAddresses.RAW_TRAIN_MAP_INDICES + i);
			m.startMapping(FileAddresses.RAW_FOLD+i, FileAddresses.RAW_FOLD_MAPPED+i);
		}
	}
	
	public static void check() throws ClassNotFoundException, IOException {
		for (int i = 0; i < 10; i++) {
			FileInputStream fos = new FileInputStream(FileAddresses.RAW_FOLD_MAPPED+i);
			ObjectInputStream oos = new ObjectInputStream(fos);
			List<Integer> acts = (List<Integer>)oos.readObject();
			List<String> patterns = (List<String>)oos.readObject();
			System.out.println(acts.size()+" == "+patterns.size());
			System.out.println(patterns.get(0)+"\n"+patterns.get(patterns.size()-1)+"\n"+patterns.get(patterns.size()/2));
		}
	}
	
	public static void main(String[] args) throws ClassNotFoundException, IOException {
//		run();
		check();
	}
}
