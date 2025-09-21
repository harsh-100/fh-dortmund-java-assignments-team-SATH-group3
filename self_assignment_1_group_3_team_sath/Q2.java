public class Q2{
	public static void main(String[] args){
		
		int nValues = 50;
		
		loop1:
		for(int i = 2; i <= nValues; i++){
			
			long square_root_of_i = Math.round(Math.sqrt(i));
			
			for (int j = 2; j <= square_root_of_i; j++){
				if (i % j == 0){
					continue loop1;
				}	
			}
			
			System.out.println(i);
		}
	}
} 