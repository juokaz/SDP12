close all;
clear all;
clc;
load('..\..\pathOutput.txt')
Traj=pathOutput;


hold on;
plot(Traj(:,1),Traj(:,2),'o','color','k','linewidth',3);
axis equal
