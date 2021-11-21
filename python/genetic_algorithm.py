import numpy as np
import math
import random


class Individual:
    def __init__(self, N):
        self.N = N # number of gens
        self.path = []
        self.time_charge = [i for i in range(N)]
        self.total_distance = 0
        self.fitness = 0
        self.fitness_GACS = 0
        self.fitness_T = 0
        self.initialize_individual()
        
       
    def initialize_individual(self):
        self.path = [i for i in range(self.N)]
        random.shuffle(self.path)
        
    
    def calculate_fitness(self, function):
        self.fitness = function(self.path)
            
    
class Popuation:
    def __init__(self, size):
        self.size = size
        self.individuals = []
        self.fitness_test = 0
    
    def initialize_population(self, number_gen):
        for i in range(self.size):
            self.individuals.append(Individual(number_gen))
        print('generate')
    
    
    def calculate_fitness(self, goal):
        for individual in self.individuals:
            individual.calculate_fitness(goal)
    
        
    def get_fitnessest(self):
        pass
        
        
        
        
# class Main:
#     def __init__(self, start, amount):
#         self.start = start
#         self.amount = amount
#         self.pops = Popuation(start, amount)
#         self.pops.initialize_population()
#         self.fittest = 0
#         self.generation = 0
#         self.survival = amount // 3
        
#         self.overfit = []


#     def selection(self):
#         self.pops.individuals.sort(key=lambda x: x.fitness)
#         del self.pops.individuals[self.survival:]
        
#         # renew
#         for indi in self.pops.individuals:
#             indi.position = self.start.astype(float)
#             indi.theta = 0
            
#         self.fittest = self.pops.individuals[0].fitness
        

#     def crossover(self, ratio):
#         # crossover_point = np.random.randint(50, 100)
#         crossover_point = ratio % self.amount
#         new = self.amount // 10
        
#         son = Individual(self.start)
#         son.gene_phi = self.pops.individuals[0].gene_phi.copy()[:crossover_point] + self.pops.individuals[1].gene_phi.copy()[crossover_point:]
#         daughter = Individual(self.start)
#         daughter.genes = self.pops.individuals[1].gene_phi.copy()[:crossover_point] + self.pops.individuals[0].gene_phi.copy()[crossover_point:]
        
#         self.pops.individuals.append(son)
#         self.pops.individuals.append(daughter)
        
#         for i in range((self.amount-self.survival)-new-2):
#             pos_dad = np.random.randint(0, self.survival)
#             pos_mom = np.random.randint(0, self.survival)

#             offspring = Individual(self.start)
#             offspring.gene_phi = self.pops.individuals[pos_dad].gene_phi.copy()[:crossover_point] + self.pops.individuals[pos_mom].gene_phi.copy()[crossover_point:]
            
#             self.pops.individuals.append(offspring)
            
        
#     def mutation(self, ratio, over):
#         number_genes = self.gene_length // np.random.randint(8, 16)
#         rang = self.amount - self.amount // 10
#         max_specific = self.gene_length -30
        
#         for i in range(ratio-3):
#             index = np.random.randint(3, rang)
#             indi = self.pops.individuals[index]
            
#             for j in range(number_genes):
#                 pos = np.random.randint(25, self.gene_length)
#                 indi.gene_phi[pos] = -indi.gene_phi[pos]
                
#         for i in range(3):
#             index = np.random.randint(3, 25)
#             indi = self.pops.individuals[index]
#             for i in range(3):
#                 if over < max_specific:
#                     pos = np.random.randint(over-8, over + 15)
#                 else:
#                     pos = np.random.randint(over-8, self.gene_length)
#                 indi.gene_phi[pos] = -indi.gene_phi[pos] 
        
#         self.pops.individuals[5].gene_phi[over-8] -= 0
#         self.pops.individuals[5].gene_phi[over-4] -= 0
                
                
#     def add_new(self):
#         new = self.amount // 10
#         for i in range(new):
#             self.pops.individuals.append(Individual(self.start))
                

#     def relate_offspring(self):
#         pass
    
    
#     def optimal_gene(self):
#         pass
    
    
#     def check_overfit(self):
#         pass
