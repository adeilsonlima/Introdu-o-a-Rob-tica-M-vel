package br.ufal.ic.robotica.cluster;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.TermCriteria;

public class Clusterization {
	public Mat cluster(Mat img, int k) {
		Mat samples = img.reshape(1, img.cols() * img.rows());
		Mat samples32f = new Mat();
		samples.convertTo(samples32f, CvType.CV_32F, 1.0 / 255.0);
		Mat labels = new Mat();
		TermCriteria criteria = new TermCriteria(TermCriteria.COUNT, 100, 1);
		Mat centers = new Mat();
		Core.kmeans(samples32f, k, labels, criteria, 1, Core.KMEANS_PP_CENTERS, centers);

		return showClusters(img, labels, centers);
	}

	private static Mat showClusters(Mat cutout, Mat labels, Mat centers) {
		centers.convertTo(centers, CvType.CV_8UC1, 255.0);
		centers.reshape(3);

		int rows = 0;
		for (int y = 0; y < cutout.rows(); y++) {
			for (int x = 0; x < cutout.cols(); x++) {
				int label = (int) labels.get(rows, 0)[0];
				int r = (int) centers.get(label, 2)[0];
				int g = (int) centers.get(label, 1)[0];
				int b = (int) centers.get(label, 0)[0];
			//	System.out.println("(" + r + "," + g + ", " + b + ")");
				cutout.put(y, x, b, g, r);
				rows++;
			}System.out.println(rows);
		}
		return cutout;
	}

}
