close all;
clear all;
clc;
load('..\..\Output.txt')
Data=Output;
Len=1;
[M,N]=size(Data(:,1:2));
a=sqrt(Data(:,3).*Data(:,3)+Data(:,4).*Data(:,4))
vec_1=Data(:,3)./a*Len;
vec_2=Data(:,4)./a*Len;
vectors=cat(2,vec_1,vec_2);
poses=cat(2,Data(:,1:2),Data(:,1:2)+vectors(:,:));

[M,N]=size(poses)
j=1;
for i=1:M
  if(~isnan(poses(i,:)))
      res_poses(j,:)= poses(i,:);
      j=j+1;
  else
      poses(i,:);
  end
end

ARROW(res_poses(:,1:2),res_poses(:,3:4),'Length',Len,'tipAngle',40,'baseAngle',70);
%hold on;
%plot(Traj(:,1),Traj(:,2),'color','k','linewidth',3);
%axis equal
