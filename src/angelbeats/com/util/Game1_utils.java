package angelbeats.com.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Game1_utils {
	private static Integer[] a=new Integer[8];
	private static Integer[] b=new Integer[8];
	public static Integer[] getId(int n)
	{
		Integer[] arryRandom = new Integer[n+1];
		a=getRandomId(8);
		for(int i =0;i<n;i++)
			 arryRandom[i]=a[i];
		while(mergeSort(0,7)%2!=0)
		{
			a=getRandomId(8);
			for(int i =0;i<n;i++)
				 arryRandom[i]=a[i];
		}
		arryRandom[8] = 0;
		return arryRandom;
	}
	public static Integer[] getRandomId(int n)
	{
		Integer[] arryRandom1 = new Integer[n];
		for (int i = 0; i < n; i++)
			arryRandom1[i] = i+1;
		List<Integer> list = Arrays.asList(arryRandom1);
		Collections.shuffle(list);
		return arryRandom1;
	} 
	public static long mergeSort(int a,int b)// 下标，例如数组int is[5]，全部排序的调用为mergeSort(0,4)
	{
		if(a<b)
		{
			int mid=(a+b)/2;
			long count=0;
			count+=mergeSort(a,mid);
			count+=mergeSort(mid+1,b);
			count+=merge(a,mid,b);
			return count;
		}
		return 0;
	}
	public static long merge(int low,int mid,int high)
	{
		int i=low,j=mid+1,k=low;
		long count=0;
		while(i<=mid&&j<=high)
			if(a[i]<=a[j])// 此处为稳定排序的关键，不能用小于
				b[k++]=a[i++];
			else
			{
				b[k++]=a[j++];
				count+=j-k;// 每当后段的数组元素提前时，记录提前的距离
			}
		while(i<=mid)
			b[k++]=a[i++];
		while(j<=high)
			b[k++]=a[j++];
		for(i=low;i<=high;i++)// 写回原数组
			a[i]=b[i];
		return count;
	}
	public static Integer[] number = getId(8);

}
