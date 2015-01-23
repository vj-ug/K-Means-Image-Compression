package com.kmeans;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import javax.imageio.ImageIO;

public class KMeans {
	public static void main(String[] args) {
		if (args.length < 3) {
			System.out
					.println("Usage: Kmeans <input-image> <k> <output-image>");
			return;
		}
		try {
			BufferedImage originalImage = ImageIO.read(new File(args[0]));
			int k = Integer.parseInt(args[1]);
			BufferedImage kmeansJpg = kmeans_helper(originalImage, k);
			ImageIO.write(kmeansJpg, "jpg", new File(args[2]));

		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	private static BufferedImage kmeans_helper(BufferedImage originalImage,
			int k) {
		int w = originalImage.getWidth();
		int h = originalImage.getHeight();
		BufferedImage kmeansImage = new BufferedImage(w, h,
				originalImage.getType());
		Graphics2D g = kmeansImage.createGraphics();
		g.drawImage(originalImage, 0, 0, w, h, null);
		// Read rgb values from the image
		int[] rgb = new int[w * h];
		int count = 0;
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				rgb[count++] = kmeansImage.getRGB(i, j);
			}
		}
		// Call kmeans algorithm: update the rgb values
		kmeans(rgb, k);

		// Write the new rgb values to the image
		count = 0;
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				kmeansImage.setRGB(i, j, rgb[count++]);
			}
		}
		return kmeansImage;
	}

	private static void kmeans(int[] rgb, final int k) {

		// rgbColor -> Image in Color array
		Color[] rgbColor = new Color[rgb.length];
		// distinctColors -> Distinct colors in the image
		HashSet<Integer> distinctColors = new HashSet<>();
		// distinctColorsList -> Distinct colors in list represented as integers
		ArrayList<Integer> distinctColorsList;
		// clusterColors -> The colors of the cluster
		Color[] clusterColors = new Color[k];
		// clusters -> Mapping of cluster color index to the list of colors in
		// that cluster
		HashMap<Integer, ArrayList<Integer>> clusters = new HashMap<Integer, ArrayList<Integer>>();

		for (int rgbIterator = 0; rgbIterator < rgb.length; rgbIterator++) {
			rgbColor[rgbIterator] = new Color(rgb[rgbIterator]);
			distinctColors.add(rgb[rgbIterator]);
		}

		distinctColorsList = new ArrayList<Integer>(distinctColors);

		for (int kIterator = 0; kIterator < k; kIterator++) {
			Random r = new Random();
			int low = 0;
			int high = distinctColors.size() - kIterator;
			int randomNo = r.nextInt(high - low) + low;

			clusterColors[kIterator] = new Color(
					distinctColorsList.remove(randomNo));
			clusters.put(kIterator, new ArrayList<Integer>());
		}

		while (true) {
			for (int index = 0; index < rgb.length; index++) {

				int minDistance = Integer.MAX_VALUE;
				int indexOfColorWithMinDistanceFromClusterColor = 0;
				int distance = 0;

				for (int kIterator = 0; kIterator < k; kIterator++) {

					double redDist, greenDist, blueDist;

					redDist = Math.pow(clusterColors[kIterator].getRed()
							- rgbColor[index].getRed(), 2);
					blueDist = Math.pow(clusterColors[kIterator].getBlue()
							- rgbColor[index].getBlue(), 2);
					greenDist = Math.pow(clusterColors[kIterator].getGreen()
							- rgbColor[index].getGreen(), 2);

					distance = (int) (redDist + greenDist + blueDist);
					
					if (minDistance > distance) {
						minDistance = distance;
						indexOfColorWithMinDistanceFromClusterColor = kIterator;
					}
				}
				
				clusters.get(indexOfColorWithMinDistanceFromClusterColor).add(
						index);
			}

			int redAverage = 0;
			int greenAverage = 0;
			int blueAverage = 0;

			int noOfChangedClusterColors = 0;
			
			for (int kIterator = 0; kIterator < k; kIterator++) {
				redAverage = 0;
				greenAverage = 0;
				blueAverage = 0;
				
				ArrayList<Integer> listOfColors = clusters.get(kIterator);
				
				for (Integer pixel : listOfColors) {
					redAverage += rgbColor[pixel].getRed();
					greenAverage += rgbColor[pixel].getGreen();
					blueAverage += rgbColor[pixel].getBlue();
				}
				
				int size = listOfColors.size();
				
				Color average = new Color( redAverage / size,greenAverage / size, blueAverage/ size);
				if (clusterColors[kIterator].getRGB() != average.getRGB())
					noOfChangedClusterColors++;
				
				clusterColors[kIterator] = average;
			}
			
			if (noOfChangedClusterColors == 0)
				break;
			
			for (int kIterator = 0; kIterator < k; kIterator++) {
				clusters.put(kIterator, new ArrayList<Integer>());
			}
		}

		// Modifying rgb values as per clustering results
		for (int kIterator = 0; kIterator < k; kIterator++) {
			ArrayList<Integer> listOfColors = clusters.get(kIterator);
			for (Integer pixel : listOfColors) {
				rgb[pixel] = clusterColors[kIterator].getRGB();
			}
		}

	}
}
