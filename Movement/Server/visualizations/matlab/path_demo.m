close all;
clear all;
clc;
load('..\..\pathOutput.txt')
Traj=pathOutput;

obs=[400,380];

plot(Traj(:,1),Traj(:,2),'o','color','k','linewidth',3);
hold on;
plot(obs(1,1),obs(1,2),'o','color','r','linewidth',3);
axis equal
