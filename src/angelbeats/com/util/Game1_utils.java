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
	public static long mergeSort(int a,int b)// �±꣬��������int is[5]��ȫ������ĵ���ΪmergeSort(0,4)
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
			if(a[i]<=a[j])// �˴�Ϊ�ȶ�����Ĺؼ���������С��
				b[k++]=a[i++];
			else
			{
				b[k++]=a[j++];
				count+=j-k;// ÿ����ε�����Ԫ����ǰʱ����¼��ǰ�ľ���
			}
		while(i<=mid)
			b[k++]=a[i++];
		while(j<=high)
			b[k++]=a[j++];
		for(i=low;i<=high;i++)// д��ԭ����
			a[i]=b[i];
		return count;
	}
	public static Integer[] number = getId(8);

}
