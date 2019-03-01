<h3>Code examples</h3>
Now we can use <b>HyperonPersistenceService</b> to create our first bundle, that will be persisted later.

<code>

    private Address createAddress() {
        var address = new Address();
        address.setCity("Lake Jackson");
        address.setStreet("Allwood St");
        address.setZipCode("77566");
        return address;
    }
    
    private Driver createDriver(Address address) {
        var dateOfBirth = Date.from(LocalDate.now().minusYears(40).atStartOfDay(ZoneId.systemDefault()).toInstant());
        return new Driver()
            .setFirstname("John")
            .setLastname("Potter")
            .setGender("M")
            .setBirthDate(dateOfBirth)
            .setLicenceObtainedAtAge(18L)
            .setNumberOfAccidents(0L)
            .setNumberOfTickets(0L)
            .setAddress(address);
    }
    
    private Vehicle createVehicle() {
        var vehicle = new Vehicle();
        vehicle.setProductionYear(2010L);
        vehicle.setMakeId(217L);
        vehicle.setTypeId(28654L);
        vehicle.setModelId(218915L);
        return vehicle;
    }
    
    private void rebuildCoverages(List<HyperonDomainObject> coverages, Option option) {
        for (var c : coverages) {
            // change that to AppCtx with coverage only
            HyperonContext ctx = new HyperonContext(
                "option.code", option.getCode(),
                "coverage.code", c.getCode()
            );

            // get IS_AVAILABLE attribute's value
            boolean isAvailable = c.getAttrBoolean("IS_AVAILABLE", ctx);

            Optional<Coverage> optionCoverage = option.getCoverages()
                .stream()
                .filter(cov -> cov.getCode().equals(c.getCode()))
                .findFirst();

            // add/update coverage only if available for this option
            if (isAvailable) {
                if (optionCoverage.isPresent()) {
                    // update
                    setCoverData(c, optionCoverage.get(), ctx);
                } else {
                    // add new
                    Coverage cover = new Coverage();
                    cover.setCode(c.getCode());
                    setCoverData(c, cover, ctx);
                    option.getCoverages().add(cover);
                }
            }

            // remove not available existing coverage
            if (!isAvailable && optionCoverage.isPresent()) {
                option.getCoverages().remove(optionCoverage.get());
            }
        }
    }
    
    private Quote createQuote(Driver driver, Vehicle vehicle) {
        var quote = new Quote();
        quote.setPlanCode(plan.getCode());
        quote.setDriver(driver);
        quote.setVehicle(vehicle);

        for (var o : options) {
            var option = new Option();
            option.setCode(o.getCode());
            option.setOrder(o.getAttribute("ORDER").getLong(new HyperonContext()));
            quote.getOptions().add(option);

            rebuildCoverages(coverages, option);
        }
        
        return quote;
    }

    private BundleRoot buildQuote(HyperonDomainObject plan, List<HyperonDomainObject> options, List<HyperonDomainObject> coverages) {

        // sample data
        var address = createAddress();
        var driver = createDriver(address);
        var vehicle = createVehicle();

        var quote = createQuote(driver, vehicle);
        var root = service.create();
        root.setQuote(quote);
        return root;
    }
</code>
<h3>Sample HyperonPersistenceService usage</h3>
This two lines are very important aspects of Hyperon Peristence.
<code>

    var root = service.create(); // here we are using HyperonPersistenceService to create BundleRoot
    root.setQuote(quote);        // and here we are setting quote, that will be managed by yperon Peristence from now on
</code>
Then this root, which is type of BundleRoot can be reused in code and any changes done to this object, or its children will be
tracked by Hyperon Peristence. After changes are done to this object, it can be persisted using:
<code>

    service.persist(bundleRoot); // service is type of HyperonPersistenceService
</code>
Each change, that was done to this bundleRoot, will be saved in application database (not in Hyperon Studio).