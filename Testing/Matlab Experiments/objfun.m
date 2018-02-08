function cost = objfun(x)

disp(x)

load noisy_and_denoised_signals.mat
 
nOriginalItems = length(denoised_original); 
nScanItems = length(denoised_scan); 

A = NaN(1, nOriginalItems); 

%transform = @(y)((y-x(2))/x(1)); 
transform = @(y)(x(1)*y + x(2)); 


for index = 1:nOriginalItems
    if((floor(transform(index)) >= 1) && (floor(transform(index)) <= nScanItems))
        A(index) = denoised_original(floor(transform(index)));
    end
end

%result = -sum( A.*denoised_original , 'omitnan')


% Take the covariance over all the non-NaN items in 


% Minimise negative covariance
EX = mean(denoised_original, 'omitnan') 
EY = mean(A, 'omitnan') 
EXEXYEY = mean((denoised_original - EX).*(A - EY), 'omitnan')

cost = EXEXYEY; 




