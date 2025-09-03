import {
    Box,
    Button,
    Container,
    Heading,
    HStack,
    Select,
    useToast,
    VStack,
    Text,
    Input,
    FormControl,
    FormLabel,
    SimpleGrid,
    GridItem,
    useDisclosure,
    Drawer,
    DrawerOverlay,
    DrawerContent,
    DrawerHeader,
    DrawerBody,
    DrawerCloseButton,
} from "@chakra-ui/react";
import { useEffect, useState } from "react";
import { RouteApi, LocationDropdown } from "../api";
import type { RouteFindItem } from "../api";

function formatLoc(l?: { locationCode?: string; name?: string; city?: string; country?: string }) {
    if (!l) return "-";
    const code = l.locationCode ? `(${l.locationCode}) ` : "";
    const tail = [l.name, l.city, l.country].filter(Boolean).join(", ");
    return `${code}${tail}`;
}

function extractVia(r: RouteFindItem): { locationCode?: string; name?: string; city?: string; country?: string } | null {
    const legs = r.transportations ?? [];
    if (legs.length < 2) return null;
    const originIds = new Map<number, any>();
    const destIds = new Map<number, any>();
    legs.forEach((t) => {
        const o = t.originLocation;
        const d = t.destinationLocation;
        if (o && typeof o.id === "number") originIds.set(o.id, o);
        if (d && typeof d.id === "number") destIds.set(d.id, d);
    });
    for (const [id, loc] of destIds.entries()) {
        if (originIds.has(id)) return loc;
    }
    const endpoints = new Set<number>([
        r.originLocation?.id as number,
        r.destinationLocation?.id as number,
    ]);
    const counts = new Map<number, { loc: any; count: number }>();
    legs.forEach((t) => {
        [t.originLocation, t.destinationLocation].forEach((loc) => {
            if (!loc || typeof loc.id !== "number") return;
            if (endpoints.has(loc.id)) return;
            const curr = counts.get(loc.id);
            counts.set(loc.id, { loc, count: (curr?.count || 0) + 1 });
        });
    });
    let best: any = null;
    let max = 0;
    counts.forEach((v) => {
        if (v.count > max) {
            max = v.count;
            best = v.loc;
        }
    });
    return best;
}

function formatVia(l?: { locationCode?: string; name?: string; city?: string; country?: string }) {
    if (!l) return "-";
    const code = l.locationCode ? `(${l.locationCode}) ` : "";
    const tail = [l.name, l.city, l.country].filter(Boolean).join(", ");
    return `Via ${code}${tail}`;
}

function buildTimeline(legs: Array<{
    originLocation: any;
    destinationLocation: any;
    transportationType: string;
}>): { nodes: any[]; edges: string[] } {
    const list = Array.isArray(legs) ? legs.slice() : [];
    if (list.length === 0) return { nodes: [], edges: [] };

    const destSet = new Set(list.map((l) => l.destinationLocation?.id).filter((x) => x != null));
    let start = list.find((l) => !destSet.has(l.originLocation?.id));
    if (!start) start = list[0]; 


    const nodes: any[] = [start.originLocation];
    const edges: string[] = [];
    const used = new Set<number>();
    let current = start;
    used.add(start.originLocation?.id ?? -1);
    const remaining = new Set(list.map((_, i) => i));

    const startIdx = list.indexOf(start);
    if (startIdx >= 0) remaining.delete(startIdx);

    while (true) {
        edges.push(current.transportationType);
        nodes.push(current.destinationLocation);
  
        let foundIdx: number | null = null;
        for (const i of remaining) {
            const leg = list[i];
            if (leg.originLocation?.id === current.destinationLocation?.id) {
                foundIdx = i; break;
            }
        }
        if (foundIdx == null) break;
        current = list[foundIdx];
        remaining.delete(foundIdx);
    }

    return { nodes, edges };
}

export default function RoutesPage({ showError }: { showError?: (e: string | { title?: string; description?: string; violations?: string[] }) => void }) {
    const toast = useToast();


    const [locationOptions, setLocationOptions] = useState<
        { id: number; name: string; city: string; country: string; locationCode: string }[]
    >([]);


    const [originId, setOriginId] = useState<number | "">("");
    const [destinationId, setDestinationId] = useState<number | "">("");
    const [date, setDate] = useState<string>("");


    const [results, setResults] = useState<RouteFindItem[]>([]);
    const [loading, setLoading] = useState(false);

    const { isOpen: isDrawerOpen, onOpen: onDrawerOpen, onClose: onDrawerClose } = useDisclosure();
    const [activeRoute, setActiveRoute] = useState<RouteFindItem | null>(null);

    useEffect(() => {
        (async () => {
            try {
                const locs = await LocationDropdown.listAllNames();
                setLocationOptions(locs);
            } catch (e: any) {
                const detail = e?.response?.data?.detail || e?.response?.data?.message || e?.message || "Load failed";
                const violations = Array.isArray(e?.response?.data?.violations)
                    ? e.response.data.violations.map((v: any) =>
                        v?.field ? `${v.field}: ${v?.message ?? ""}`.trim() : (v?.message ?? String(v))
                    )
                    : undefined;
                showError?.({ title: "Load failed", description: detail, violations });
            }
        })();
    }, []);

    const onSearch = async () => {
        if (!originId || !destinationId) {
            toast({ status: "warning", title: "Please select origin and destination" });
            return;
        }
        try {
            setLoading(true);
            const data = await RouteApi.find({
                originLocationId: Number(originId),
                destinationLocationId: Number(destinationId),
                ...(date ? { date } : {}),
            });
            setResults(data ?? []);
        } catch (e: any) {
            const detail = e?.response?.data?.detail || e?.response?.data?.message || e?.message || "Search failed";
            const violations = Array.isArray(e?.response?.data?.violations)
                ? e.response.data.violations.map((v: any) =>
                    v?.field ? `${v.field}: ${v?.message ?? ""}`.trim() : (v?.message ?? String(v))
                )
                : undefined;
            showError?.({ title: "Search failed", description: detail, violations });
        } finally {
            setLoading(false);
        }
    };

    return (
        <Container maxW="6xl" py={8}>
            <HStack justify="space-between" mb={4} wrap="wrap" spacing={3}>
                <Heading size="lg">Routes</Heading>
            </HStack>

            {}
            <Box border="1px" borderColor="gray.200" borderRadius="md" bg="white" p={5} mb={6}>
                <SimpleGrid columns={{ base: 1, md: 3, lg: 4 }} spacing={4} alignItems="end">
                    {}
                    <GridItem>
                        <FormControl>
                            <FormLabel fontWeight="semibold">Origin</FormLabel>
                            <Select
                                placeholder="Select origin"
                                value={originId}
                                onChange={(e) => setOriginId(e.target.value ? Number(e.target.value) : "")}
                                size="md"
                                bg="white"
                            >
                                {locationOptions.map((l) => (
                                    <option key={l.id} value={l.id}>
                                        ({l.locationCode}) {l.name}, {l.city}, {l.country}
                                    </option>
                                ))}
                            </Select>
                        </FormControl>
                    </GridItem>

                    {}
                    <GridItem>
                        <FormControl>
                            <FormLabel fontWeight="semibold">Destination</FormLabel>
                            <Select
                                placeholder="Select destination"
                                value={destinationId}
                                onChange={(e) => setDestinationId(e.target.value ? Number(e.target.value) : "")}
                                size="md"
                                bg="white"
                            >
                                {locationOptions.map((l) => (
                                    <option key={l.id} value={l.id}>
                                        ({l.locationCode}) {l.name}, {l.city}, {l.country}
                                    </option>
                                ))}
                            </Select>
                        </FormControl>
                    </GridItem>

                    {}
                    <GridItem>
                        <FormControl>
                            <FormLabel fontWeight="semibold">Date</FormLabel>
                            <Input
                                type="date"
                                value={date || ""}
                                onChange={(e) => setDate(e.target.value)}
                                size="md"
                                bg="white"
                            />
                        </FormControl>
                    </GridItem>

                    {}
                    <GridItem>
                        <Button colorScheme="blue" size="md" onClick={onSearch} isLoading={loading} width="100%">
                            Search
                        </Button>
                    </GridItem>
                </SimpleGrid>
            </Box>

            <Heading size="md" mb={3}>
                {results.length > 0
                    ? `Available Routes: ${formatLoc(locationOptions.find(l => l.id === Number(originId)))} → ${formatLoc(locationOptions.find(l => l.id === Number(destinationId)))}`
                    : "Available Routes"}
            </Heading>

            {results.length === 0 ? (
                <Box border="1px" borderColor="gray.200" borderRadius="md" bg="white" p={6}>
                    <Text color="gray.600">{loading ? "Searching..." : "No routes yet. Please search."}</Text>
                </Box>
            ) : (
                <>
                    <VStack align="stretch" spacing={4}>
                        {results.map((r, idx) => (
                            <Box
                                key={idx}
                                border="1px"
                                borderColor="gray.200"
                                borderRadius="md"
                                bg="white"
                                overflow="hidden"
                            >

                                <Box
                                    px={4}
                                    py={4}
                                    role="button"
                                    cursor="pointer"
                                    _hover={{ bg: "gray.50" }}
                                    onClick={() => {
                                        setActiveRoute(r);
                                        onDrawerOpen();
                                    }}
                                >
                                    <Text color="blue.600" fontWeight="medium">
                                        {formatVia(extractVia(r) ?? undefined)}
                                    </Text>
                                </Box>
                            </Box>
                        ))}
                    </VStack></>
            )}

            {}
            <Drawer placement="right" onClose={onDrawerClose} isOpen={isDrawerOpen} size="md">
                <DrawerOverlay />
                <DrawerContent>
                    <DrawerCloseButton />
                    <DrawerHeader>Route Details</DrawerHeader>
                    <DrawerBody>
                        {activeRoute ? (
                            (() => {
                                const tl = buildTimeline(activeRoute.transportations || []);
                                if (!tl.nodes.length) {
                                    return <Text color="gray.600">No steps available for this route.</Text>;
                                }
                                return (
                                    <VStack align="stretch" spacing={0}>
                                        {tl.nodes.map((loc, idx) => (
                                            <Box key={idx}>
                                                {}
                                                <HStack align="flex-start" spacing={3}>
                                                    <Box mt={1} boxSize={3} borderWidth="2px" borderColor="gray.400" borderRadius="full" />
                                                    <Text fontWeight={idx === 0 || idx === tl.nodes.length - 1 ? "semibold" : "normal"}>
                                                        {formatLoc(loc)}
                                                    </Text>
                                                </HStack>

                                                {}
                                                {idx < tl.edges.length && (
                                                    <HStack align="flex-start" spacing={3} ml={0} mt={1} mb={2}>
                                                        <Box ml={1.5} minW={3}>
                                                            <Box borderLeftWidth="2px" borderStyle="dotted" borderColor="gray.300" height={6} ml={1} />
                                                        </Box>
                                                        <Text fontSize="sm" color="gray.600">{tl.edges[idx]}</Text>
                                                    </HStack>
                                                )}
                                            </Box>
                                        ))}
                                    </VStack>
                                );
                            })()
                        ) : (
                            <Text color="gray.600">Select a route to see details.</Text>
                        )}
                    </DrawerBody>
                </DrawerContent>
            </Drawer>
        </Container>
    );
}