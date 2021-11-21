import numpy as np
import random

from model_problem import WCE, Sensor
from genetic_algorithm import *
from operators import *
from utils import *


class GACS:
    def __init__(self):
        ## constant
        self.number_sensor = 150
        self.crossover_rate = 0.7
        self.mutation_rate = 0.1
        self.PMX_rate = 0.5
        self.anpha = 0.5
        self.crossover_rate_phase2 = 0.75
        self.mutation_rate_phase2 = 0.1
        self.num_generation_phase2 = 1000
        
        ## parameters of GA
        self.N = self.number_sensor # length of gen = number of sensor 
        self.pop_size = 20
        self.num_generation = 400
        self.best = 0
        
        
        ## map
        self.map = read_file('evolution_computation\project\genetic_algorithm\data.txt') ## pos, P, E
        pos, P, E = self.map
        self.depot = -1
        self.sensors = [Sensor(pos[i], P[i], E[i]) for i in range(self.number_sensor)]
        self.sensors.append(Sensor(np.array([500, 500]), 0, 888))
        self.WCE = WCE()
        self.max_time_charge_sensor = [i for i in range(self.N)]
        for i in range(self.N):
            self.max_time_charge_sensor[i] = (self.sensors[i].E_max - self.sensors[i].E_min) / (self.WCE.U - self.sensors[i].P)
        
    
    def fitnessFGACS(self, path):
        N = self.number_sensor
        f1 = 0
        f2 = 0
        f1s = [0 for i in range(N)]
        weight = [0 for i in range(N)]
        total_distance = distance(self.sensors[-1].position, self.sensors[path[0]].position)
        for i in range(N):
            sensor1 = path[i]
            sensor2 = 0
            if i == N - 1:
                sensor2 = -1
            else:
                sensor2 = path[i+1]
            
            weight[sensor1] = self.sensors[sensor1].E / self.sensors[sensor1].P
            waiting_time = total_distance / self.WCE.V
            f1s[sensor1] = waiting_time / weight[sensor1]
            f1 += f1s[sensor1]
            total_distance += distance(self.sensors[sensor1].position, self.sensors[sensor2].position)
        
        for sensor in path:
            f2 += abs(f1s[sensor] - f1 / N)
        
        return f1 * self.anpha + (1- self.anpha)*f2, total_distance
    
    
    def fitness_T(self, path, time_charge):
        N = self.N
        death = 0
        accumalate_distance = distance(self.sensors[self.depot].position, self.sensors[path[0]].position)
        waiting_time = 0
        died = []
        for i in range(N):
            sensor1 = path[i]
            sensor2 = 0
            if i == N - 1:
                sensor2 = -1
            else:
                sensor2 = path[i+1]
            
            waiting_time += distance(self.sensors[sensor1].position, self.sensors[sensor2].position) / self.WCE.V
            accumalate_distance += distance(self.sensors[sensor1].position, self.sensors[sensor2].position)
            
            if (self.sensors[i].E - waiting_time * self.sensors[i].P) < self.sensors[i].E_min:
                death += 1
                died.append(i)
            waiting_time += time_charge[i]
            
        # them thoi gian tu diem cuoi den dich
        waiting_time += distance(self.sensors[path[-1]].position, self.sensors[-1].position)
        
        for i in range(N):
            sensor1 = path[i]

            if i in died:
                continue
            if (self.sensors[sensor1].E + time_charge[i] * self.WCE.U - waiting_time * self.sensors[sensor1].P) < self.sensors[sensor1].E_min:
                death += 1
            
        return death
        
    
    def phase1(self, population: Popuation):
        offsprings = []
        for i in range(self.pop_size):
            rand = np.random.uniform()
            if rand < self.crossover_rate:
                p1 = np.random.randint(self.pop_size)
                p2 = np.random.randint(self.pop_size)

                rand_crossover_algorithm = np.random.uniform()
                if rand_crossover_algorithm < self.PMX_rate:
                    offsprings.extend(PMX(population.individuals[p1], population.individuals[p2]))
                else:
                    offsprings.extend(SPX(population.individuals[p1], population.individuals[p2]))
        population.individuals.extend(offsprings)
        
        mutate = []
        for i in range(self.pop_size):
            rand = np.random.uniform()
            if rand < self.mutation_rate:
                p = np.random.randint(self.pop_size)

                rand_mutation_algorithm = np.random.uniform()
                if rand_mutation_algorithm < 0.5:
                    mutate.append(CIM(population.individuals[p]))
                else:
                    mutate.append(swap_mutation(population.individuals[p]))
                    
        population.individuals.extend(mutate)
        for ind in population.individuals:
            ind.fitness_GACS, ind.total_distance = self.fitnessFGACS(ind.path)
        population.individuals = sorted(population.individuals, key=lambda x: x.fitness_GACS)[:self.pop_size]
    
    
    def phase2(self, population: Popuation):
        N = self.N
        ## get best path
        best_ind = deepcopy(population.individuals[0]) 
        best_path = best_ind.path
        best_distance = best_ind.total_distance
        
        ## tính tổng năng lượng di chuyển và tổng thời gian còn lại
        total_travel_energy = best_distance * self.WCE.P_M / self.WCE.V
        total_t = (self.WCE.E_MC - total_travel_energy) / self.WCE.U
        
        ## init population
        pop = []
        for i in range(self.pop_size):
            ind = Individual(N)
            ind.path = best_path
            ind.total_distance = best_distance
            pop.append(ind)
            
        ## tính thời gian sạc cho cá thể theo thứ tự đường đi
        for ind in pop:
            gen = [i for i in range(N)]
            remain = total_t
            for i in range(N):
                r = min(np.random.uniform(0, remain), self.max_time_charge_sensor[i])
                gen[i] = r
                remain -= r
            random.shuffle(gen)
            ind.time_charge = gen
            
        timestamp = 0
        while timestamp < 30:
            ## crossover
            for i in range(self.pop_size):
                rand = np.random.uniform()
                if rand < self.crossover_rate_phase2:
                    p1 = np.random.randint(self.pop_size)
                    p2 = np.random.randint(self.pop_size)
                    pop.extend(SPAH(pop[p1], pop[p2]))
                    
            ## mutaion
            for i in range(self.pop_size):
                rand = np.random.uniform()
                if rand < self.mutation_rate_phase2:
                    pop[i] = mutation2(pop[i])
                    
            for ind in pop:
                ind.fitness_T =  self.fitness_T(best_path, ind.time_charge)
            pop = sorted(pop, key=lambda x: x.fitness_T)
            
            timestamp += 1
        
        self.best = pop[0]
        
    
    def execute(self):
        pop = Popuation(self.pop_size)
        pop.initialize_population(self.N)
        timestamp = 1
        all_time = 20
        while timestamp < all_time:
            print('generation: ', timestamp)
            self.phase1(pop)
            # for i in range(2):
            #     print(pop.individuals[i].path, pop.individuals[i].fitness_GACS)
            timestamp += 1
        self.phase2(pop)
        print('best path: ', self.best.path)
        print('best distance: ', self.best.total_distance)
        print('best death: ', self.best.fitness_T)
        

if __name__ == '__main__':
    gacs = GACS()
    # N = 10
    # ind1 = Individual(N)
    # ind2 = Individual(N)
    # ind1.path = [i for i in range(N)]
    # ind2.path = [i for i in range(N)]
    # random.shuffle(ind1.path)
    # random.shuffle(ind2.path)
    
    # print(ind1.path)
    # print(ind2.path)
    # print()
    
    # off1, off2 = SPX(ind1, ind2)
    # print()
    # print(off1.path)
    # print(off2.path)
    
    gacs.execute()
    