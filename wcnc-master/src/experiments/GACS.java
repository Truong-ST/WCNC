package experiments;

import java.lang.Math;
import utils.Factor;
import wrsn.Individual;
import wrsn.Map;
import wrsn.Population;
import wrsn.Sensor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class GACS extends Algorithm {
    @Override
    public void execute(Map map, Factor factor) {
        // Khoi tao quan the ban dau
//        System.out.println(Factor.N);
        Population P = new Population(Factor.N, map);
        double timestamp = 0;
        Random rand = new Random();
        double sumDistance;
//        Ngẫu nhiên các cá thể ban đầu
        for (int i = 0; i < 5; i++) {
            int r = rand.nextInt(P.getN());
            Individual ind = P.getIndividuals().get(r);
            ind.calculateTotalDistance(map);
            sumDistance = ind.getTotalDistance();
            System.out.println("distance: "+sumDistance);
        }
        while (timestamp < Factor.T) {
            phase1(P, map, factor);
            timestamp ++;
            
        }
        System.out.println("---------------Het pha 1----------------");
        phase2(P, map, factor);
        System.out.println("Het 1 chu trình");
    }

    private void phase1(Population P, Map map, Factor factor) {
        int populationSize = P.getN();  // kich thuoc quan the
        int N = map.getN(); // so luong sensor

        // Tien hanh lai ghep va dot bien
        ArrayList<Individual> offspingIndividuals = new ArrayList<>();
        ArrayList<Individual> mutationIndividuals = new ArrayList<>();

        Random rand= new Random();
//        rand.setSeed(Factor.SEED);
        for (int i = 0; i < populationSize; i++) {
            double rmx = rand.nextDouble();
            // xac suat de lai ghep
            if (rmx < Factor.CROSSOVER_RATE_1) {
                int parent1 = rand.nextInt(populationSize);
                int parent2 = rand.nextInt(populationSize);
                double rmpmx = rand.nextDouble();
                if (rmpmx < Factor.PMX_RATE) {
                    // lai ghep pmx
                    offspingIndividuals.addAll(PMX(P.getIndividual(parent1), P.getIndividual(parent2)));
                    double rmx1 = rand.nextDouble();
                    // xac suat de lai ghep
                    if (rmx1 < Factor.MUTATION_RATE_1) {
                        int parent = rand.nextInt(offspingIndividuals.size());
                        double rmpmx1 = rand.nextDouble();
                        if (rmpmx1 < Factor.SWAP_RATE) {
                            // dot bien swap
                        	mutationIndividuals.addAll(CIM(offspingIndividuals.get(parent)));
                        }
                        else {
                            // dot bien CIM
                        	mutationIndividuals.addAll(swapMutation(offspingIndividuals.get(parent)));
                        	
                        }
                    }
                }
                else {
                    // lai ghep spx
                    offspingIndividuals.addAll(SPX(P.getIndividual(parent1), P.getIndividual(parent2)));
                    double rmx1 = rand.nextDouble();
                    // xac suat de lai ghep
                    if (rmx1 < Factor.MUTATION_RATE_1) {
                        int parent = rand.nextInt(offspingIndividuals.size());
                        double rmpmx1 = rand.nextDouble();
                        if (rmpmx1 < Factor.SWAP_RATE) {
                            // dot bien swap
                        	mutationIndividuals.addAll(CIM(offspingIndividuals.get(parent)));
                        }
                        else {
                            // dot bien CIM
                        	mutationIndividuals.addAll(swapMutation(offspingIndividuals.get(parent)));
                        	
                        }
                    }
                }
            }
        }
        // add hết vào quần thể
        P.getIndividuals().addAll(offspingIndividuals);
        P.getIndividuals().addAll(mutationIndividuals);
        
        for (Individual ind: P.getIndividuals()) {
        	ind.calculateFitnessF(map);
        }
        //Sắp xếp theo fitness
        P.getIndividuals().sort(Comparator.comparing(Individual::getFitnessF));//sx theo fit
        System.out.println("best fitness: " + P.getIndividual(0).getFitnessF());
        
        for (Individual ind: P.getIndividuals()) {
        	ind.calculateTotalDistance(map);
        }
//        P.getIndividuals().sort(Comparator.comparing(Individual::getTotalDistance));//sx theo distance
        System.out.println("best distance: " + P.getIndividual(0).getTotalDistance());
//        System.out.println(P.getIndividual(0).getPath());
//        System.out.println("best distance 2: " + P.getIndividual(1).getTotalDistance());
//        System.out.println("best distance 3: " + P.getIndividual(2).getTotalDistance());
//        System.out.println("best distance 100: " + P.getIndividual(99).getTotalDistance());
//        System.out.println(P.getIndividual(100).getPath());
        
        //Chọn tốt nhất
        ArrayList<Individual> new_P = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            new_P.add(P.getIndividual(i));
        }
        P.setIndividuals(new_P);
    }
    
   

    private ArrayList<Individual> PMX(Individual parent1, Individual parent2) {
    	
        int N = parent1.getN();
        Random rand= new Random();
        rand.setSeed(Factor.SEED);
        int point1 = rand.nextInt(N); // chon ngau nhien diem cat thu nhat
        int point2 = rand.nextInt(N); // chon ngau nhien diem cat thu hai
        Individual offspring1 = new Individual(parent1);
        Individual offspring2 = new Individual(parent2);
//        for (int i = 0; i < N; i++) {
//        	System.out.print(parent1.getNode(i));
//        	System.out.print("\t"+"|");
//        }
//        System.out.println('\n');
//        for (int i = 0; i < N; i++) {
//        	System.out.print(parent2.getNode(i));
//        	System.out.print("\t"+"|");
//        }
//        System.out.println('\n');
//        System.out.println("CUT: "+Integer.toString(point1)+"\t"+""+Integer.toString(point2));
        
//        for (int i = 0; i < N; i++) {
//        	System.out.print(parent1.getNode(i));
//        	System.out.print('|');
//        }
//        System.out.println('\n');
//        System.out.println("========================================");

        //Ä�oáº¡n giá»¯a cá»§a má»—i cÃ¡ thá»ƒ con
        if(point1>point2) {
        	int temp=point1;
        	point1=point2;
        	point2=temp;
        	
        	
        }
        for (int i = 0; i < N; i++) {
            if(i>=point1 && i<=point2){
                offspring1.setNode(i, parent2.getNode(i));
                offspring2.setNode(i, parent1.getNode(i));
            }
            else{
                offspring1.setNode(i, -1);
                offspring2.setNode(i, -1);

            }
        }
//        for (int i = 0; i < N; i++) {
//        	System.out.print(offspring2.getNode(i));
//        	System.out.print('|');
//        }
//        System.out.println('\n');
//        System.out.println("========================================");

        //Con 1 á»Ÿ bÃªn trÃ¡i vÃ  bÃªn pháº£i Ä‘iá»ƒm cáº¯t
        for (int k = point1; k <= point2; k++) {
            //náº¿u k cÃ³ trong con rá»“i thÃ¬ qua Ä‘i
            int exist=0;
            for(int j = 0; j < N; j++){
                if(parent1.getNode(k)==offspring1.getNode(j)){
                    exist=1;
                }
            }
            if(exist==1) continue;
            int tmp1 = parent2.getNode(k);
            int find = 0;
            while(find==0){
                for(int j = 0; j < N; j++){
                    if(parent1.getNode(j)==tmp1){
                        if(j>=point1 && j<=point2){
                            tmp1 = parent2.getNode(j);
                            break;
                        }
                        else{
                            offspring1.setNode(j, parent1.getNode(k));
                            find=1;
                            break;
                        }

                    }
                }
            }
        }
//        for (int i = 0; i < N; i++) {
//        	System.out.print(offspring1.getNode(i));
//        	System.out.print("\t"+"|");
//        }
//        System.out.println('\n');
        
        //con 1 phan con lai
        for(int i = 0; i < N; i++){
            int tmp2 = parent1.getNode(i);
            int exist2 = 0;
            for(int j = 0; j < N; j++){
                if (offspring1.getNode(j)==tmp2) exist2=1;
            }
            if(exist2==1) continue;
            else{
                for(int x = 0; x < N; x++){
                    if(offspring1.getNode(x)==-1){
                        offspring1.setNode(x, tmp2);
                        break;
                    }
                }
            }
        }
        
        //Con 2 á»Ÿ bÃªn trÃ¡i vÃ  bÃªn pháº£i Ä‘iá»ƒm cáº¯t
        for (int k = point1; k <= point2; k++) {
            //náº¿u k cÃ³ trong con rá»“i thÃ¬ qua Ä‘i
            int exist=0;
            for(int j = 0; j < N; j++){
                if(parent2.getNode(k)==offspring2.getNode(j)){
                    exist=1;
                }
            }
            if(exist==1) continue;
            int tmp3 = parent1.getNode(k);
            int find = 0;
            while(find==0){
                for(int j = 0; j < N; j++){
                    if(parent2.getNode(j)==tmp3){
                        if(j>=point1 && j<=point2){
                            tmp3 = parent1.getNode(j);
                            break;
                        }
                        else{
                            offspring2.setNode(j, parent2.getNode(k));
                            find=1;
                            break;
                        }

                    }
                }
            }
        }

//        for (int i = 0; i < N; i++) {
//        	System.out.print(offspring2.getNode(i));
//        	System.out.print("\t"+"|");
//        }
//        System.out.println('\n');
        //Ä‘oáº¡n Ä‘iá»�n pháº§n cÃ²n thiáº¿u cho con 2
        for(int i = 0; i < N; i++){
            int tmp4 = parent2.getNode(i);
            int exist2 = 0;
            for(int j = 0; j < N; j++){
                if (offspring2.getNode(j)==tmp4) exist2=1;
            }
            if(exist2==1) continue;
            else{
                for(int x = 0; x < N; x++){
                    if(offspring2.getNode(x)==-1){
                        offspring2.setNode(x, tmp4);
                        break;
                    }
                }
            }
        }
        
//        for (int i = 0; i < N; i++) {
//        	System.out.print(offspring1.getNode(i));
//        	System.out.print("\t"+"|");
//        }
//        System.out.println('\n');
//
//        for (int i = 0; i < N; i++) {
//        	System.out.print(offspring2.getNode(i));
//        	System.out.print("\t"+"|");
//        }
//        System.out.println('\n');
//        System.out.println("========================================");
        return new ArrayList<Individual>() {{
            add(offspring1);
            add(offspring2);
        }};
    }

    private ArrayList<Individual> SPX(Individual parent1, Individual parent2) {
        int N = parent1.getN();
        Random rand= new Random();
        rand.setSeed(Factor.SEED);
        int point = rand.nextInt(N); // chon ngau nhien mot diem cat
        Individual offspring1 = new Individual(parent2);
        Individual offspring2 = new Individual(parent1);
//        int[] location1 = new int[N - point]; // vi tri cua cac sensor ben phai diem cat con 1
//        int[] location2 = new int[N - point]; // vi tri cua cac sensor ben phai diem cat con 2
//        int pointSize = N - point; //kich thuoc ben phai diem cat cua cac con
//        int loc1Count = 0;
//        int loc2Count = 0;
//
//        // Xac dinh thu tu ben phai diem cat cua con 1 tren cha me 2
//        for (int i = 0; i < N; i++) {
//            if (loc1Count == pointSize) break;
//            for (int j = point; j < N; j++) {
//                if (parent1.getNode(i) == offspring1.getNode(j)) {
//                    location1[loc1Count] = parent1.getNode(i);
//                    loc1Count ++;
//                }
//            }
//        }
//
//        // Xac dinh thu tu ben phai diem cat cua con 2 tren cha me 1
//        for (int i = 0; i < N; i++) {
//            if (loc2Count == pointSize) break;
//            for (int j = point; j < N; j++) {
//                if (parent2.getNode(i) == offspring2.getNode(j)) {
//                    location2[loc2Count] = parent2.getNode(i);
//                    loc2Count ++;
//                }
//            }
//        }
//
//        for (int i = point; i < N; i++) {
//            offspring1.setNode(i, location1[i - point]);
//            offspring2.setNode(i, location2[i - point]);
//        }
        for (int i = point; i < N; i++) {
          offspring1.setNode(i, -1);
          offspring2.setNode(i, -1);
        }
        //con 1
        for(int i = 0; i < N; i++){
            int tmp2 = parent1.getNode(i);
            int exist2 = 0;
            for(int j = 0; j < N; j++){
                if (offspring1.getNode(j)==tmp2) exist2=1;
            }
            if(exist2==1) continue;
            else{
                for(int x = 0; x < N; x++){
                    if(offspring1.getNode(x)==-1){
                        offspring1.setNode(x, tmp2);
                        break;
                    }
                }
            }
        }
        
        for(int i = 0; i < N; i++){
            int tmp4 = parent2.getNode(i);
            int exist2 = 0;
            for(int j = 0; j < N; j++){
                if (offspring2.getNode(j)==tmp4) exist2=1;
            }
            if(exist2==1) continue;
            else{
                for(int x = 0; x < N; x++){
                    if(offspring2.getNode(x)==-1){
                        offspring2.setNode(x, tmp4);
                        break;
                    }
                }
            }
        }
        
        
//        for (int i = 0; i < N; i++) {
//        	System.out.print(parent1.getNode(i));
//        	System.out.print("\t"+"|");
//        }
//        System.out.println('\n');
//        for (int i = 0; i < N; i++) {
//        	System.out.print(parent2.getNode(i));
//        	System.out.print("\t"+"|");
//        }
//        System.out.println('\n');
//        System.out.println("CUT: "+Integer.toString(point));
//        
//        for (int i = 0; i < N; i++) {
//        	System.out.print(offspring1.getNode(i));
//        	System.out.print("\t"+"|");
//        }
//        System.out.println('\n');
//
//        for (int i = 0; i < N; i++) {
//        	System.out.print(offspring2.getNode(i));
//        	System.out.print("\t"+"|");
//        }
//        System.out.println('\n');
//        System.out.println("========================================");
        return new ArrayList<Individual>() {{
            add(offspring1);
            add(offspring2);
        }};
    }
    private ArrayList<Individual> CIM(Individual parent){
    
    	
        int N = parent.getN();
        Random rand= new Random();
        rand.setSeed(Factor.SEED);
        int point = rand.nextInt(N);
        Individual offspring = new Individual(parent);
//        for (int i = 0; i < N; i++) {
//        	System.out.print(parent.getNode(i));
//        	System.out.print("\t"+"|");
//        }
//        System.out.println('\n');

        for (int i = 0; i <= point; i++) {
            offspring.setNode(i, parent.getNode(point-i));
        }
        for (int i = point+1; i < N; i++) {
            offspring.setNode(i, parent.getNode(N-i+point));
        }
//        for (int i = 0; i < N; i++) {
//        	System.out.print(offspring.getNode(i));
//        	System.out.print("\t"+"|");
//        }
//        System.out.println('\n');
//        System.out.println("===============");
        return new ArrayList<Individual>() {{
            add(offspring);
        }};
    }

    private ArrayList<Individual> swapMutation(Individual parent){
        int N = parent.getN();
        Random rand= new Random();
        rand.setSeed(Factor.SEED);
        int point1 = rand.nextInt(N); // chon ngau nhien diem cat thu nhat
        int point2 = rand.nextInt(N); // chon ngau nhien diem cat thu hai
        Individual offspring = new Individual(parent);
//        for (int i = 0; i < N; i++) {
//        	System.out.print(parent.getNode(i));
//        	System.out.print("\t"+"|");
//        }
//        System.out.println('\n');
        int tmp = parent.getNode(point1);
   
        offspring.setNode(point1, parent.getNode(point2));
        offspring.setNode(point2, tmp);
//        for (int i = 0; i < N; i++) {
//        	System.out.print(offspring.getNode(i));
//        	System.out.print("\t"+"|");
//        }
//        System.out.println('\n');
//        System.out.println("===============");

        return new ArrayList<Individual>() {{
            add(offspring);
        }};

    }


    private static void phase2(Population P, Map map, Factor factor) {
        int populationSize = P.getN();  // kich thuoc quan the
        int N = map.getN(); // so luong sensor
        Random rand = new Random();

        // get best path
        double bestDistance = 100000000;
        ArrayList<Integer> bestPath = new ArrayList<>();
        double sumDistance;
        for (int i = 0; i < 5; i++) {
            int r = rand.nextInt(P.getN());
            Individual ind = P.getIndividuals().get(r);
            ind.calculateTotalDistance(map);
            sumDistance = ind.getTotalDistance();
//            System.out.println("distance: "+sumDistance);
//            System.out.println(ind.getPath());
        }
        for (Individual ind: P.getIndividuals()) {
            ind.calculateTotalDistance(map);
            sumDistance = ind.getTotalDistance();
            if (sumDistance < bestDistance){
                bestDistance = sumDistance;
                bestPath = ind.getPath();
            }
        }
//        System.out.println("best: " + bestDistance);
//        System.out.println("best: " + bestPath);
        
        double totalEnergyTravel = bestDistance * map.getWce().P_M / map.getWce().V;
        ArrayList<Double> maxTimeCharge = new ArrayList<>(N);
        for (int i = 0; i < N; i++) {
            maxTimeCharge.add(0.0);
        }

        // init number
        double total_t = (map.getWce().E_MC - totalEnergyTravel)/map.getWce().U;
//        System.out.println("===== Total : "+ total_t);
//        System.out.println("===== Emc : "+ map.getWce().E_MC );
        System.out.println("===== totalEtravel : "+ totalEnergyTravel );
        for (int i = 0; i < N; i++) {
            maxTimeCharge.set(i, (Sensor.E_MAX - Sensor.E_MIN)/ (map.getWce().U - Sensor.P));
        }
//        System.out.println("===== E : "+ Sensor.E_MAX );
//        System.out.println("===== Emin : "+ Sensor.E_MIN );
//        System.out.println("===== U : "+ map.getWce().U );
//        System.out.println("===== p : "+ Sensor.P );
        
        // init pop
        ArrayList<Individual> pop = new ArrayList<>(N);
        for (int i = 0; i < populationSize; i++) {
            pop.add(new Individual(P.getIndividual(0)));
        }
        System.out.println("total_T: " + total_t);
        for (Individual ind: pop) {
            ArrayList<Double> gen = new ArrayList<>(N);
            for (int i = 0; i < N; i++) {
                gen.add(0.0);
            }
//            double remain = total_t;
//            for (int i = 0; i < N; i++) {
//                Double r = rand.nextDouble() * Math.min( remain, maxTimeCharge.get(i));
//                gen.set(i, r);
//                remain = remain - r;
//            }
            Double sum = 0.0;
            for (int i = 0; i < N; i++) {
                Double r = rand.nextDouble() * maxTimeCharge.get(i);
                sum += r;
                ind.setTau(i, r);
            }
            for (int i = 0; i < N; i++) {
                ind.setTau(i, ind.getTau(i) * total_t / sum);
            }

//            ind.setTaus(gen);
        }
        P.setIndividuals(pop);
        
        int timestamp = 0;
        while (timestamp < 20){
        	pop = P.getIndividuals();
        	
            timestamp++;
            // tinh fitness va chon loc
            for (Individual individual : pop) {
                individual.calculateFitnessT(individual.getTaus(), map);
            }

            // crossover
            ArrayList<Individual> offspingIndividuals = new ArrayList<>();
//            rand.setSeed(Factor.SEED);
            for (int i = 0; i < populationSize; i++) {
                double rmx = rand.nextDouble();
                // xac suat de lai ghep
                if (rmx < Factor.CROSSOVER_RATE_1) { 
                    int parent1 = rand.nextInt(pop.size());
                    int parent2 = rand.nextInt(pop.size());
                    
                    offspingIndividuals.addAll(SPAH(pop.get(parent1), pop.get(parent2)));
                }
            }
            pop.addAll(offspingIndividuals);
            System.out.println(pop.size());
            // mutation
            for (int i = 0; i < pop.size(); i++) {
                double rmx = rand.nextDouble();
                // xac suat de dot bien
                if (rmx < Factor.MUTATION_RATE_1) {
                    pop.set(i, mutation2(pop.get(i), maxTimeCharge));
                }
            }
            for (Individual individual : pop) {
                individual.calculateFitnessT(individual.getTaus(), map);
            }
            Collections.shuffle(pop, new Random());
            pop.sort(Comparator.comparing(Individual::getFitnessT));
            
            System.out.println("best dead nodes T1: " + pop.get(0).getFitnessT());
            System.out.println("best dead nodes T1: " + pop.get(0).getTaus());
//            System.out.println("best dead nodes T2: " + pop.get(100).getFitnessT());
//            System.out.println("best dead nodes T3: " + pop.get(4900).getFitnessT());
            
           ArrayList<Individual> new_P = new ArrayList<>();
           for (int i = 0; i < populationSize; i++) {
                new_P.add(pop.get(i));
           }

            P.setIndividuals(new_P);
//            P.setIndividuals(pop);

        }
//        System.out.println(pop.get(0).getPath());
//        System.out.println(pop.get(0).getTaus());
//        

    }
    
//Lai ghép p2
    private static ArrayList<Individual> SPAH(Individual parent1, Individual parent2){
        Individual offspring1 = new Individual(parent1);
        Individual offspring2 = new Individual(parent2);
//        System.out.println("cha 1: "+parent1.getTaus().stream().mapToDouble(f -> f.doubleValue()).sum());
//        System.out.println("cha 1: "+parent1.getTaus());
//        System.out.println("cha 2: "+parent2.getTaus().stream().mapToDouble(f -> f.doubleValue()).sum());
//        System.out.println("cha 2: "+parent2.getTaus());

        for (int i = 0; i < parent1.getN(); i++) {
        	double b = (Math.random());
//            System.out.println("b: "+b);
            offspring1.setTau(i, (1-b)*parent1.getTau(i)+b*parent2.getTau(i));
            offspring2.setTau(i, b*parent1.getTau(i)+(1-b)*parent2.getTau(i));
        }
        // Nếu không thỏa mãn ràng buộc 23, 24 thì phải chuẩn hóa
        double total_t = parent1.getTaus().stream().mapToDouble(f -> f.doubleValue()).sum();
        double totalevent = offspring1.getTaus().stream().mapToDouble(f -> f.doubleValue()).sum();
//        if(total_t<0) System.out.println("loi roi 1");
//        if(totalevent<0) System.out.println("loi roi 2");
        for (int i = 0; i < parent1.getN(); i++) {
        	offspring1.setTau(i, offspring1.getTau(i)*total_t/totalevent);
            offspring2.setTau(i, offspring2.getTau(i)*total_t/totalevent);
        }
        totalevent = offspring1.getTaus().stream().mapToDouble(f -> f.doubleValue()).sum();
//        System.out.println("con 1: "+totalevent);
//        System.out.println("con 1: "+offspring1.getTaus());
        //

        return new ArrayList<Individual>() {{
            add(offspring1);
            add(offspring2);
        }};

    }

//Đột biến p2
    private static Individual mutation2(Individual parent, ArrayList<Double> maxTimeCharge){
        int N = parent.getN();
        Individual offspring = new Individual(parent);
        Random rand= new Random();
        rand.setSeed(Factor.SEED);
        int i = rand.nextInt(N); // chon ngau nhien diem cat thu nhat
        int j = rand.nextInt(N); // chon ngau nhien diem cat thu hai

        double r1 = Math.random()*(maxTimeCharge.get(i)-parent.getTau(i));
        double r2 = Math.random()*(parent.getTau(j));

        double r=0;
        if(r1<r2) r=r1;
        else r=r2;
        
        if (parent.getTau(j)-r<0) System.out.println("Lỗi số đột biến âm");
        offspring.setTau(i, parent.getTau(i)+r);
        offspring.setTau(j, parent.getTau(j)-r);

        return offspring;
    }



}
