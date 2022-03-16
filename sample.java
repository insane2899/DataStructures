import com.soumik.algorithms.sorts.InsertionSort;

public class sample{
	public static void main(String[] args){
		Integer[] array = {4,5,6,3,2,7,8,9,1};
		InsertionSort.sort(array);
		for(Integer x:array){
			System.out.print(x+" ");
		}
		System.out.println();

	}
}