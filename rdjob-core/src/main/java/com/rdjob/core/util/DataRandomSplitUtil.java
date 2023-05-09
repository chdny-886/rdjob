package com.rdjob.core.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class DataRandomSplitUtil {

	/**
	 * 需要写一个数据随机拆分方法： 1、输入参数有四个变量，a（long），b（int），m（float）， c（float），
	 * 2、其中a表示总数额（会很大），b表示拆分个数，m表示最小浮动范围，c表示最大浮动范围
	 * 3、该方法会根据b的值，将a拆分为b个随机数值（long），比如：x1，x2，x3......xb，
	 * 其中，这些数之间最大差额不会超过c的浮动范围，比如m为0.25，c为0.35，x1为最大值，x2为最小值，，
	 * x1与x2相差幅度最小不会低于25%，最大不会超过35%的比例，在25%~35%之间。 4、该方法返回 拆分之后的x1，x2，x3，....xb
	 * 数列，并且由小到大排序 5、由于是long型，允许存在误差，即x1+....xb的总和可以不等于a，但是误差在10以内最好
	 * @param a
	 * @param b
	 * @param m
	 * @param c
	 * @return
	 */
	public static Float[] bigDataSplit(float a, float b, float m, float c) {
		//long startTime = System.currentTimeMillis();
		boolean flag = true;
		List<Float> result = new ArrayList<>();
		HashSet<Float> different;
		float sum = 0, max, min;
		float realC = 0L;
		int count = 0;
		//如果误差范围合理并且没有重复值，退出循环
		while (flag) {
			count++;
			sum = 0L;
			result = split(a, b, m, c);
			min = result.get(0);
			max = result.get(result.size() - 1);
			different = new HashSet<>();
			for (int i = 0; i < result.size(); i++) {
				Float res = result.get(i);
				sum += res;
				different.add(res);
			}
			float divide = (max - min) / min;
			realC = divide;

			if ( (divide - c) <= 0 && (m - divide) <= 0 && different.size() == b && Math.abs(sum - a) < 10) {
				flag = false;
			}
			if (count >= 100000000)
				break;
		}
		Float[] objects = result.toArray(new Float[0]);
		quickSort(objects, 0, objects.length - 1);
		//long endTime = System.currentTimeMillis();
		//System.out.println("误差值：" + (sum - a));
		//System.out.println("浮动差额范围：" + m + "~" + c);
		//System.out.println("实际最大差额：" + realC);
		//System.out.println("计算次数：" + count);
		//System.out.println("计算时间：" + (endTime - startTime) + "ms");
		return objects;
	}

	private static List<Float> split(float a, float b, float m, float c) {
		List<Float> list = new ArrayList<>();
		int cnt = 0;
		float average = a / b;
		float x = mockFloatBetween(m, c);

		float min = (2 / (2+x)) * average;
		float sum = 0;
		while (b-- > 1) {
			float res = mockFloatBetween(1.0f, 1 + x) * min;
			list.add(cnt++, res);
			sum = sum + res;
		}
		float dif = sum - a;
		dif = Math.abs(dif);
		list.add(cnt++, dif);
		return list;
	}

	private static float mockFloatBetween(float begin, float end) {
		float bigDecimal = end - begin;
		float point = (float) Math.random();
		float pointBetween = point * bigDecimal;
		//float result = pointBetween.add(new BigDecimal(begin)).setScale(2, BigDecimal.ROUND_FLOOR);
		float result = pointBetween + begin;
		return result;
	}

	private static void quickSort(Float[] arrays, int left, int right) {
		if (left > right)
			return;
		int i = left;
		int j = right;
		float base = arrays[left];
		while (i != j) {
			while (i < j && arrays[j] >= base) {
				j--;
			}
			while (i < j && arrays[i] <= base) {
				i++;
			}
			swap(arrays, i, j);
		}
		arrays[left] = arrays[i];
		arrays[i] = base;
		quickSort(arrays, left, i - 1);
		quickSort(arrays, i + 1, right);
	}

	private static void swap(Float[] arr, int i, int j) {
		float temp = arr[i];
		arr[i] = arr[j];
		arr[j] = temp;
	}

	//public static void main(String[] args) {
	//	float a = 1300000;
	//	float b = 800;
    //    float m = 0.9f;
    //    float c = 3.9f;
    //    Float[] result = DataRandomSplitUtil.bigDataSplit(a, b, m, c);
    //    for (int i = 0; i < result.length; i++) {
    //        System.out.println(i + 1 + "," + result[i]);
    //    }
	//}

}
