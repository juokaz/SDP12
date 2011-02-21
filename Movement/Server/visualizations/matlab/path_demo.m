close all;
clear all;
clc;
load('..\..\pathOutput.txt')
Traj=pathOutput;

obs=[300,550];

plot(Traj(:,1),Traj(:,2),'-ro','linewidth',1.5);
hold on;

axis equal

load('..\..\pathOutputExtended.txt')
Traj=pathOutputExtended;



plot(Traj(:,1),Traj(:,2),'-.b','linewidth',1.5);


plot(obs(1,1),obs(1,2),'o','color','g','linewidth',3);
legend('Normal Potential Field','Extended Potential Field','Obstacle Position');

hold on;
axis equal