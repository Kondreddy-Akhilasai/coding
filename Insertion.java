public class InsertionSort {
    public static void main(String[] args) {
        int[] arr = {5, 2, 9, 1};
        for(int i = 1; i < arr.length; i++) {
            int key = arr[i], j = i - 1;
            while(j >= 0 && arr[j] > key)
                arr[j + 1] = arr[j--];
            arr[j + 1] = key;
        }
        for(int val : arr)
            System.out.print(val + " ");
    }
}
