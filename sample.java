import com.soumik.algorithms.sorts.InsertionSort;
import com.soumik.algorithms.data_structures.QuakeHeap;

public class sample{
	public static void main(String[] args)throws Exception{
		Integer[] array = {4,5,6,3,2,7,8,9,1};
		InsertionSort.sort(array);
		for(Integer x:array){
			System.out.print(x+" ");
		}
		System.out.println();
		QuakeHeap<String,Integer> heap = new QuakeHeap<>(5);
		QuakeHeap.Locator l1 = heap.insert("BWI",88);
		heap.insert("LAX",42);
		heap.insert("IAD",26);
		heap.insert("DCA",67);
		heap.insert("JFK",94);
		heap.insert("ATL",48);
		heap.insert("SFO",19);
		System.out.println(heap.listHeap());
		System.out.println(heap.getMaxLevel(l1));
		System.out.println(heap.getMinKey());

	}
}